package br.elibrary.stateless;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import br.elibrary.dto.BookDTO;
import br.elibrary.dto.CopyDTO;
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

	@Override
	public BookDTO create(BookDTO dto) {
		
		if (dto == null) 
			return null;

		Book entity = BookMapper.toEntity(dto, em);
		em.persist(entity);

		catalogStatusSB.onBookCreated();
		
		return BookMapper.toDTO(entity);
	}

	@Override
	public BookDTO update(BookDTO dto) {
		
	    if (dto == null || dto.getId() == null)
	        throw new IllegalArgumentException("ID do livro é obrigatório para atualização");

	    Book existing = em.find(Book.class, dto.getId());
	    if (existing == null)
	        throw new IllegalArgumentException("Livro não encontrado");

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

	    return BookMapper.toDTO(existing);
	}

	@Override
	public void delete(BookDTO book) {

		Book managed = em.find(Book.class, book.getId());
		if (managed == null)
			return;

		long totalCopies = managed.getCopies().size();
		long availableCopies = managed.getCopies().stream().filter(c -> c.getStatus() == CopyStatus.AVAILABLE).count();

		em.remove(managed);
		catalogStatusSB.onBookDeleted((int) totalCopies, (int) availableCopies);
	}

	@Override
	public BookDTO findById(Long id) {
		Book book = em.createQuery("""
				SELECT b FROM Book b
				LEFT JOIN FETCH b.categories
				WHERE b.id = :id
				""", Book.class).setParameter("id", id).getSingleResult();
		
		return BookMapper.toDTO(book);
	}

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

	@Override
	public CopyDTO findFirstAvailableCopy(Long bookId) {
		
		Copy copy = em.createQuery("""
				SELECT c FROM Copy c
				WHERE c.book.id = :bookId AND c.status = :status
				""", Copy.class).setParameter("bookId", bookId).setParameter("status", CopyStatus.AVAILABLE)
				.setMaxResults(1).getResultStream().findFirst().orElse(null);
		
		return CopyMapper.toDTO(copy);
	}
	
	@Override
	public List<BookDTO> findByAuthorOrCategory(String author, String categoryName) {
		
	    StringBuilder jpql = new StringBuilder("""
	        SELECT DISTINCT b FROM Book b
	        LEFT JOIN b.categories cat
	        WHERE 1 = 1
	        """);
	    
	    if (author != null && !author.trim().isEmpty()) {
	        jpql.append(" AND LOWER(b.author) LIKE LOWER(:author)");
	    }
	    
	    if (categoryName != null && !categoryName.trim().isEmpty()) {
	        jpql.append(" AND LOWER(cat.name) LIKE LOWER(:categoryName)");
	    }

	    TypedQuery<Book> query = em.createQuery(jpql.toString(), Book.class);

	    if (author != null && !author.trim().isEmpty()) {
	        query.setParameter("author", "%" + author.trim() + "%");
	    }

	    if (categoryName != null && !categoryName.trim().isEmpty()) {
	        query.setParameter("categoryName", "%" + categoryName.trim() + "%");
	    }

	    List<Book> books = query.getResultList();
	    return books.stream()
	                .map(BookMapper::toDTO)
	                .collect(Collectors.toList());
	}
}