package br.elibrary.stateless;

import java.util.List;
import java.util.stream.Collectors;

import br.elibrary.dto.CopyDTO;
import br.elibrary.exception.BusinessException;
import br.elibrary.mapper.CopyMapper;
import br.elibrary.model.Book;
import br.elibrary.model.Copy;
import br.elibrary.model.enuns.CopyStatus;
import br.elibrary.service.CatalogStatusService;
import br.elibrary.service.CopyService;
import br.elibrary.service.LoanService;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Session Bean Stateless responsável pelo gerenciamento de exemplares (cópias físicas).
 * 
 * Coordena o ciclo de vida do exemplar, incluindo a máquina de estados de disponibilidade.
 */
@Stateless
public class CopySB implements CopyService {

	@PersistenceContext(unitName = "E-Library")
	private EntityManager em;

	@EJB
	private CatalogStatusService catalogStatusSB;
	
	@EJB
	private LoanService loanService;
	
	/**
	 * Cadastra um novo exemplar para um livro específico.
	 * Valida a existência do livro e a unicidade do código interno.
	 * 
	 * @param dto Dados do novo exemplar.
	 * @return CopyDTO do exemplar persistido.
	 */
	@Override
	public CopyDTO create(CopyDTO dto) {

		if (dto == null)
			throw new IllegalArgumentException("Dados do exemplar são obrigatórios.");

		if (dto.getBookId() == null)
			throw new IllegalArgumentException("Livro é obrigatório.");

		Book book = em.find(Book.class, dto.getBookId());
		if (book == null)
			throw new IllegalArgumentException("Livro não encontrado. ID: " + dto.getBookId());

		if (dto.getInternalCode() == null || dto.getInternalCode().trim().isEmpty())
			dto.setInternalCode("COPY-" + System.currentTimeMillis());

		Long count = em.createQuery("SELECT COUNT(c) FROM Copy c WHERE c.internalCode = :code", Long.class)
				.setParameter("code", dto.getInternalCode().trim()).getSingleResult();

		if (count > 0)
			throw new IllegalArgumentException("Código interno já existe: " + dto.getInternalCode());

		if (dto.getStatus() == null)
			dto.setStatus(CopyStatus.AVAILABLE);

		Copy entity = CopyMapper.toEntity(dto, em);

		em.persist(entity);
		em.flush();

		catalogStatusSB.onCopyCreated();

		return CopyMapper.toDTO(entity);
	}
	
	/**
	 * Atualiza os dados de um exemplar existente, incluindo seu vínculo com o livro.
	 * 
	 * Valida se a transição de status solicitada é permitida pela regra de negócio.
	 */
	@Override
	public CopyDTO update(CopyDTO dto) {

		if (dto == null || dto.getId() == null)
			throw new IllegalArgumentException("ID da cópia é obrigatório para atualização.");

		Copy existing = em.find(Copy.class, dto.getId());

		if (existing == null)
			throw new IllegalArgumentException("Cópia não encontrada.");

		CopyStatus newStatus = dto.getStatus();
		CopyStatus oldStatus = existing.getStatus();

		if (oldStatus != null && newStatus != null) {
			if (!isValidTransition(oldStatus, newStatus))
				throw new BusinessException(String.format("Transição inválida: %s → %s", oldStatus, newStatus));
		}

		existing.setInternalCode(dto.getInternalCode());
		existing.setStatus(dto.getStatus());

		if (dto.getBookId() != null) {

			Long currentBookId = (existing.getBook() != null) ? existing.getBook().getId() : null;

			if (!dto.getBookId().equals(currentBookId)) {
				Book newBook = em.getReference(Book.class, dto.getBookId());
				existing.setBook(newBook);
			}

		} else
			throw new IllegalArgumentException("O livro da cópia não pode ser nulo.");
		
		validateCopyState(existing.getId(), dto.getStatus());

		if (oldStatus != existing.getStatus())
			catalogStatusSB.onCopyStatusChanged(oldStatus, existing.getStatus());
		
		em.flush();          
	    em.refresh(existing);

		return CopyMapper.toDTO(existing);
	}
	
	/**
	 * Altera exclusivamente o status de um exemplar.
	 * 
	 * É o método principal utilizado pelo fluxo de empréstimo e reserva.
	 */
	@Override
	public CopyDTO updateStatus(Long id, CopyStatus newStatus) {

		if (id == null) {
			throw new IllegalArgumentException("ID da cópia é obrigatório.");
		}

		if (newStatus == null) {
			throw new IllegalArgumentException("Novo status é obrigatório.");
		}

		Copy existing = em.find(Copy.class, id);

		if (existing == null) {
			throw new IllegalArgumentException("Cópia não encontrada.");
		}

		CopyStatus oldStatus = existing.getStatus();

		if (!isValidTransition(oldStatus, newStatus)) {
			throw new BusinessException(String.format("Transição inválida: %s → %s", oldStatus, newStatus));
		}
		
		validateCopyState(existing.getId(), newStatus);

		existing.setStatus(newStatus);

		if (!oldStatus.equals(newStatus)) {
			catalogStatusSB.onCopyStatusChanged(oldStatus, newStatus);
		}
		
		em.flush();          
	    em.refresh(existing);

		return CopyMapper.toDTO(existing);
	}
	
	/**
	 * Remove um exemplar pelo ID.
	 *  
	 * Impede a remoção caso o exemplar esteja atualmente emprestado.
	 */
	@Override
	public void delete(CopyDTO dto) {

		if (dto == null || dto.getId() == null)
			return;

		deleteById(dto.getId());
	}	
	
	/**
	 * Remove um exemplar pelo ID
	 */
	@Override
	public void deleteById(Long id) {

		Copy copy = em.find(Copy.class, id);
		
		if (copy == null) {
			return;
		}

		if (CopyStatus.BORROWED.equals(copy.getStatus())) {
			throw new BusinessException("Não é permitido excluir um exemplar emprestado.");
		}

		CopyStatus status = copy.getStatus();
		em.remove(copy);
		em.flush();
		catalogStatusSB.onCopyDeleted(status);
	}
	
	/**
     * Retorna o DTO de um exemplar específico buscando pelo seu ID primário.
     * 
     * @param id Identificador único da cópia.
     */
	@Override
	public CopyDTO findById(Long id) {
		Copy copy = em.find(Copy.class, id);
		return CopyMapper.toDTO(copy);
	}
	
	/**
     * Retorna a lista de todos os exemplares cadastrados no sistema.
     * 
     * Utiliza FETCH JOIN com 'book' para evitar o problema de N+1 consultas (performance).
     */
	@Override
	public List<CopyDTO> findAll() {

		List<Copy> copies = em
				.createQuery("SELECT c FROM Copy c LEFT JOIN FETCH c.book ORDER BY c.internalCode", Copy.class)
				.getResultList();

		return copies.stream().map(CopyMapper::toDTO).collect(Collectors.toList());
	}
	
	/**
     * Busca todos os exemplares que pertencem a um livro específico através do ID do livro.
     */
	@Override
	public List<CopyDTO> findByBookId(Long bookId) {

		if (bookId == null)
			return List.of();

		List<Copy> copies = em
				.createQuery("SELECT c FROM Copy c WHERE c.book.id = :bookId ORDER BY c.internalCode", Copy.class)
				.setParameter("bookId", bookId).getResultList();

		return copies.stream().map(CopyMapper::toDTO).collect(Collectors.toList());
	}
	
	/**
     * Filtra exemplares em todo o sistema baseando-se no status (ex: todos os EMPRESTADOS).
     * 
     * Também utiliza FETCH JOIN para carregar os dados do livro associado.
     */
	@Override
	public List<CopyDTO> findByStatus(CopyStatus status) {

		if (status == null)
			return List.of();

		List<Copy> copies = em.createQuery(
				"SELECT c FROM Copy c LEFT JOIN FETCH c.book WHERE c.status = :status ORDER BY c.internalCode",
				Copy.class).setParameter("status", status).getResultList();

		return copies.stream().map(CopyMapper::toDTO).collect(Collectors.toList());
	}
	
	/**
	 * Recupera todos os exemplares de um livro específico que estão disponíveis para empréstimo.
	 */
	@Override
	public List<CopyDTO> findAvailableCopiesByBookId(Long bookId) {

		if (bookId == null)
			return List.of();

		List<Copy> copies = em.createQuery("""
				SELECT c FROM Copy c
				LEFT JOIN FETCH c.book
				WHERE c.book.id = :bookId
				  AND c.status = :available
				ORDER BY c.internalCode
				""", Copy.class).setParameter("bookId", bookId).setParameter("available", CopyStatus.AVAILABLE)
				.getResultList();

		return copies.stream().map(CopyMapper::toDTO).collect(Collectors.toList());
	}
	
	/**
     * Filtra exemplares de um livro específico por um status fornecido via String.
     */
	@Override
	public List<CopyDTO> findByBookIdAndStatus(Long bookId, String statusStr) {

		if (bookId == null)
			return List.of();

		CopyStatus status = CopyStatus.valueOf(statusStr);

		List<Copy> copies = em.createQuery("""
				SELECT c FROM Copy c
				WHERE c.book.id = :bookId AND c.status = :status
				ORDER BY c.internalCode
				""", Copy.class).setParameter("bookId", bookId).setParameter("status", status).getResultList();

		return copies.stream().map(CopyMapper::toDTO).collect(Collectors.toList());
	}
	
	/**
	 * Define a máquina de estados oficial do sistema.
	 * 
	 * Fluxo permitido: 
	 * 
	 * DISPONÍVEL -> RESERVADO -> EMPRESTADO -> DISPONÍVEL.
	 */
	private boolean isValidTransition(CopyStatus from, CopyStatus to) {
		return switch (from) {
		case AVAILABLE -> to == CopyStatus.RESERVED;
		case RESERVED -> to == CopyStatus.BORROWED;
		case BORROWED -> to == CopyStatus.AVAILABLE;
		default -> false;
		};
	}
	
	/**
	 * Método auxiliar para validar se o exemplar pode sofrer alterações de status
	 * 
	 * baseado em regras de negócio e vínculos com empréstimos.
	 */
	private void validateCopyState(Long copyId, CopyStatus newStatus) {
		
	    if (newStatus != CopyStatus.BORROWED) {
	        if (loanService.hasActiveLoan(copyId)) {
	            throw new BusinessException("Operação negada: Este exemplar possui um empréstimo ativo e não pode ter o status alterado pelo administrador.");
	        }
	    }
	}
}