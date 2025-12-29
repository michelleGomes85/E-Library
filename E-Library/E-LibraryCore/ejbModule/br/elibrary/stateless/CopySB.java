package br.elibrary.stateless;

import java.util.List;
import java.util.stream.Collectors;

import br.elibrary.dto.CopyDTO;
import br.elibrary.mapper.CopyMapper;
import br.elibrary.model.Book;
import br.elibrary.model.Copy;
import br.elibrary.model.enuns.CopyStatus;
import br.elibrary.service.CatalogStatusService;
import br.elibrary.service.CopyService;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class CopySB implements CopyService {

	@PersistenceContext(unitName = "E-Library")
	private EntityManager em;

	@EJB
	private CatalogStatusService catalogStatusSB;

	@Override
	public CopyDTO create(CopyDTO dto) {
		
		if (dto == null)
	        throw new IllegalArgumentException("Dados do exemplar são obrigatórios.");
	    
		if (dto.getInternalCode() == null || dto.getInternalCode().trim().isEmpty())
		    dto.setInternalCode("COPY-" + System.currentTimeMillis());

	    if (dto.getBookId() == null)
	        throw new IllegalArgumentException("Livro é obrigatório.");
	    
	    Book book = em.find(Book.class, dto.getBookId());
	    if (book == null)
	        throw new IllegalArgumentException("Livro não encontrado. ID: " + dto.getBookId());
	    
	    Long count = em.createQuery(
	            "SELECT COUNT(c) FROM Copy c WHERE c.internalCode = :code", Long.class)
	            .setParameter("code", dto.getInternalCode().trim())
	            .getSingleResult();
	   
	    if (count > 0)
	        throw new IllegalArgumentException("Código interno já existe: " + dto.getInternalCode());
	    
		Copy entity = CopyMapper.toEntity(dto, em);
		em.persist(entity);

		catalogStatusSB.onCopyCreated();

		return CopyMapper.toDTO(entity);
	}

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
	            throw new IllegalArgumentException(
	                String.format("Transição inválida: %s → %s", oldStatus, newStatus));
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

	    if (oldStatus != existing.getStatus())
	        catalogStatusSB.onCopyStatusChanged(oldStatus, existing.getStatus());

	    return CopyMapper.toDTO(existing);
	}

	@Override
	public void delete(CopyDTO dto) {

		if (dto == null || dto.getId() == null)
			return;

		deleteById(dto.getId());
	}

	@Override
	public void deleteById(Long id) {

		Copy copy = em.find(Copy.class, id);

		if (copy != null) {
			CopyStatus status = copy.getStatus();
			em.remove(copy);
			catalogStatusSB.onCopyDeleted(status);
		}
	}

	@Override
	public CopyDTO findById(Long id) {
		Copy copy = em.find(Copy.class, id);
		return CopyMapper.toDTO(copy);
	}

	@Override
	public List<CopyDTO> findAll() {

		List<Copy> copies = em
				.createQuery("SELECT c FROM Copy c LEFT JOIN FETCH c.book ORDER BY c.internalCode", Copy.class)
				.getResultList();

		return copies.stream().map(CopyMapper::toDTO).collect(Collectors.toList());
	}

	@Override
	public List<CopyDTO> findByBookId(Long bookId) {

		if (bookId == null)
			return List.of();

		List<Copy> copies = em
				.createQuery("SELECT c FROM Copy c WHERE c.book.id = :bookId ORDER BY c.internalCode", Copy.class)
				.setParameter("bookId", bookId).getResultList();

		return copies.stream().map(CopyMapper::toDTO).collect(Collectors.toList());
	}

	@Override
	public List<CopyDTO> findByStatus(CopyStatus status) {

		if (status == null)
			return List.of();

		List<Copy> copies = em.createQuery(
				"SELECT c FROM Copy c LEFT JOIN FETCH c.book WHERE c.status = :status ORDER BY c.internalCode",
				Copy.class).setParameter("status", status).getResultList();

		return copies.stream().map(CopyMapper::toDTO).collect(Collectors.toList());
	}

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
	
	@Override
	public List<CopyDTO> findByBookIdAndStatus(Long bookId, String statusStr) {
		
	    if (bookId == null || statusStr == null)
	        return List.of();
	    
	    CopyStatus status = CopyStatus.valueOf(statusStr);
	    
	    List<Copy> copies = em.createQuery("""
	        SELECT c FROM Copy c
	        WHERE c.book.id = :bookId AND c.status = :status
	        ORDER BY c.internalCode
	        """, Copy.class)
	        .setParameter("bookId", bookId)
	        .setParameter("status", status)
	        .getResultList();
	    
	    return copies.stream().map(CopyMapper::toDTO).collect(Collectors.toList());
	}
	
	private boolean isValidTransition(CopyStatus from, CopyStatus to) {
	    return switch (from) {
	        case AVAILABLE -> to == CopyStatus.RESERVED;
	        case RESERVED  -> to == CopyStatus.BORROWED;
	        case BORROWED  -> to == CopyStatus.AVAILABLE;
	        default -> false;
	    };
	}
}