package br.elibrary.stateless;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import br.elibrary.dto.BookDTO;
import br.elibrary.dto.CopyDTO;
import br.elibrary.exception.BusinessException;
import br.elibrary.mapper.BookMapper;
import br.elibrary.mapper.CopyMapper;
import br.elibrary.model.Book;
import br.elibrary.model.Category;
import br.elibrary.model.Copy;
import br.elibrary.model.enuns.CopyStatus;
import br.elibrary.service.BookService;
import br.elibrary.service.CatalogStatusService;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Stateless
public class BookSB implements BookService {

	@PersistenceContext(unitName = "E-Library")
	private EntityManager em;

	@EJB
	private CatalogStatusService catalogStatusSB;
	
	/**
     * Verifica internamente se um ISBN já existe na base de dados.
     * 
     * @param isbn Código identificador único do livro. 
     * @return true se o ISBN já existir, false caso contrário.
     */
	private boolean verifyExistingISBN(String isbn) {
		
		Book existing = em.createQuery("SELECT b FROM Book b WHERE b.isbn = :isbn", Book.class)
                .setParameter("isbn", isbn)
                .getResultStream()
                .findFirst()
                .orElse(null);
		
		if (existing != null)
			return true;
		
		return false;
	}
	
	private boolean isEmpty(String val) {
	    return val == null || val.trim().isEmpty();
	}

	/**
     * Cria um novo livro no sistema após validar a unicidade do ISBN.
     * 
     * Notifica o serviço de status do catálogo após a criação.
     * 
     * @param dto Objeto de transferência de dados do livro.
     * @return BookDTO do livro persistido.
     */
	@Override
	public BookDTO create(BookDTO dto) {

		if (dto == null) 
	        return null;
		
		if (isEmpty(dto.getTitle()) || isEmpty(dto.getAuthor()) || isEmpty(dto.getIsbn())) {
	        throw new BusinessException("Campos obrigatórios ausentes: Titulo, Autor e ISBN são necessários.");
	    }

	    if (dto.getIsbn() != null) {
	    	
	    	if (verifyExistingISBN(dto.getIsbn()))
	    		throw new BusinessException("ISBN já cadastrado: " + dto.getIsbn());
	    }

	    Book entity = BookMapper.toEntity(dto, em);
	    em.persist(entity);
	    em.flush();

	    catalogStatusSB.onBookCreated();
	    return BookMapper.toDTO(entity);
	}
	
	/**
     * Atualiza os dados de um livro existente 
     * 
     * (ISBN, título, autor, editora, ano e categorias).
     * 
     * Valida se o novo ISBN (caso alterado) já pertence a outro livro.
     */
	@Override
	public BookDTO update(BookDTO dto) {
		
	    if (dto == null || dto.getId() == null)
	        throw new IllegalArgumentException("ID do livro é obrigatório para atualização");

	    Book existing = em.find(Book.class, dto.getId());
	    
	    if (existing == null)
	        throw new IllegalArgumentException("Livro não encontrado");
	    
	    if (!existing.getIsbn().equals(dto.getIsbn())) {
		    if (verifyExistingISBN(dto.getIsbn()))
	    		throw new BusinessException("ISBN já cadastrado: " + dto.getIsbn());
	    }

	    existing.setIsbn(dto.getIsbn());
	    existing.setTitle(dto.getTitle());
	    existing.setAuthor(dto.getAuthor());
	    existing.setPublisher(dto.getPublisher());
	    existing.setYear(dto.getYear());

	    existing.getCategories().clear();

	    if (dto.getCategoryIds() != null && !dto.getCategoryIds().isEmpty()) {
	    	
	        var managedCategories = dto.getCategoryIds().stream()
	            .filter(Objects::nonNull)
	            .map(id -> em.getReference(Category.class, id))
	            .collect(Collectors.toList());
	        existing.getCategories().addAll(managedCategories);
	    }
	    
	    em.flush();          
	    em.refresh(existing);

	    return BookMapper.toDTO(existing);
	}
	
	/**
     * Remove um livro do sistema. 
     * 
     * Impede a remoção se houver exemplares com status EMPRESTADO ou RESERVADO.
     */
	@Override
	public void delete(BookDTO book) {
		
	    Book managed = em.find(Book.class, book.getId());
	    
	    if (managed == null)
	        throw new BusinessException("Livro não encontrado para exclusão. ID: " + book.getId());

	    boolean possessesBlockedCopies = managed.getCopies().stream().anyMatch(c -> c.getStatus() == CopyStatus.BORROWED || c.getStatus() == CopyStatus.RESERVED);

	    if (possessesBlockedCopies) {
	        throw new BusinessException("Não é possível excluir o livro '" + managed.getTitle() + 
	                "'. Existem exemplares emprestados ou reservados.");
	    }

	    long totalCopies = managed.getCopies().size();
	    long availableCopies = managed.getCopies().stream()
	            .filter(c -> c.getStatus() == CopyStatus.AVAILABLE)
	            .count();

	    em.remove(managed);
	    
	    em.flush();
	    
	    catalogStatusSB.onBookDeleted((int) totalCopies, (int) availableCopies);
	}

	/**
     * Busca um livro pelo ID, carregando suas categorias de forma ansiosa (fetch join).
     */
	@Override
	public BookDTO findById(Long id) {
		
		try {
			Book book = em.createQuery("""
					SELECT b FROM Book b
					LEFT JOIN FETCH b.categories
					WHERE b.id = :id
					""", Book.class).setParameter("id", id).getSingleResult();
			
			return BookMapper.toDTO(book);
		} catch (Exception e) {
			throw new BusinessException("ID Inexistente: " + id);
		}
		
	}
	
	/**
     * Lista todos os livros ordenados por título, incluindo categorias.
     */
	@Override
	public List<BookDTO> findAll() {
		List<Book> books = em.createQuery("""
				SELECT DISTINCT b FROM Book b
				LEFT JOIN FETCH b.categories
				ORDER BY b.title
				""", Book.class)
				.getResultList();
		
		return books.stream()
				.map(BookMapper::toDTO)
				.collect(Collectors.toList());
	}
	
	/**
     * Pesquisa livros por título ou autor e retorna estatísticas de cópias 
     * (Total e Disponíveis).
     */
	@Override
	public List<Object[]> findByTitleOrAuthorWithStats(String query) {

		String q = "%" + query.toLowerCase() + "%";

		List<Object[]> results =  em.createQuery("""
				SELECT b,
				       COUNT(c) AS totalCopies,
				       SUM(CASE WHEN c.status = :available THEN 1 ELSE 0 END) AS availableCopies
				FROM Book b
				LEFT JOIN b.copies c
				WHERE LOWER(b.title) LIKE :q OR LOWER(b.author) LIKE :q
				GROUP BY b.id, b.title
				ORDER BY b.title ASC
				""", Object[].class).setParameter("q", q).setParameter("available", CopyStatus.AVAILABLE)
				.getResultList();
		
	    return results.stream()
	            .map(row -> new Object[]{
	                BookMapper.toDTO((Book) row[0]),
	                row[1], 
	                row[2]  
	            })
	            
	            .collect(Collectors.toList());
	}
	
	/**
     * Retorna livros que não possuem exemplares disponíveis 
     * 
     * (usado para lista de espera).
     */
	@Override
	public List<Object[]> findUnavailableBooksWithStats() {
		
		List<Object[]> results = em.createQuery("""
				SELECT b,
				       COUNT(c) AS totalCopies,
				       0L AS availableCopies
				FROM Book b
				INNER JOIN b.copies c
				GROUP BY b.id, b.title
				HAVING SUM(CASE WHEN c.status = :available THEN 1 ELSE 0 END) = 0
				ORDER BY b.title
				""", Object[].class).setParameter("available", CopyStatus.AVAILABLE).getResultList();
		
	    return results.stream()
	            .map(row -> new Object[]{
	                BookMapper.toDTO((Book) row[0]),
	                row[1],
	                row[2]
	            })
	            .collect(Collectors.toList());
	}
	
	/**
     * Busca livros que possuem alguma copia disponível
     */
	@Override
	public List<Object[]> findBooksWithCopyStats() {
		List<Object[]> results = em.createQuery("""
				SELECT b,
				       COUNT(c) AS totalCopies,
				       SUM(CASE WHEN c.status = :available THEN 1 ELSE 0 END) AS availableCopies
				FROM Book b
				LEFT JOIN b.copies c
				GROUP BY b.id, b.title
				ORDER BY b.title ASC
				""", Object[].class).setParameter("available", CopyStatus.AVAILABLE).getResultList();
		
	    return results.stream()
	            .map(row -> new Object[]{
	                BookMapper.toDTO((Book) row[0]),
	                row[1],
	                row[2]
	            })
	            .collect(Collectors.toList());
	}
	
	/**
     * Busca um exemplar disponível (status AVAILABLE) para um determinado livro.
     */
	@Override
	public CopyDTO findFirstAvailableCopy(Long bookId) {
		
		Copy copy = em.createQuery("""
				SELECT c FROM Copy c
				WHERE c.book.id = :bookId AND c.status = :status
				""", Copy.class).setParameter("bookId", bookId).setParameter("status", CopyStatus.AVAILABLE)
				.setMaxResults(1).getResultStream().findFirst().orElse(null);
		
		return CopyMapper.toDTO(copy);
	}
	
	/**
     * Filtra livros por autor e/ou nome da categoria.
     */
	@Override
	public List<BookDTO> findByAuthorOrCategory(String author, String categoryName) {
		
	    StringBuilder jpql = new StringBuilder("SELECT DISTINCT b FROM Book b ");
	    
	    boolean hasCategoryFilter = categoryName != null && !categoryName.trim().isEmpty();

	    if (hasCategoryFilter) {
	        jpql.append("JOIN b.categories cat ");
	    } else {
	        jpql.append("LEFT JOIN b.categories cat ");
	    }

	    jpql.append("WHERE 1 = 1");

	    if (author != null && !author.trim().isEmpty()) {
	        jpql.append(" AND LOWER(b.author) LIKE LOWER(:author)");
	    }

	    if (hasCategoryFilter) {
	        jpql.append(" AND LOWER(cat.name) LIKE LOWER(:categoryName)");
	    }

	    TypedQuery<Book> query = em.createQuery(jpql.toString(), Book.class);

	    if (author != null && !author.trim().isEmpty()) {
	        query.setParameter("author", "%" + author.trim() + "%");
	    }

	    if (hasCategoryFilter) {
	        query.setParameter("categoryName", "%" + categoryName.trim() + "%");
	    }

	    List<Book> books = query.getResultList();
	    return books.stream()
	                .map(BookMapper::toDTO)
	                .collect(Collectors.toList());
	}
}