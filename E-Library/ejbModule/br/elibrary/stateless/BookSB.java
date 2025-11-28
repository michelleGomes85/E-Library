package br.elibrary.stateless;

import java.util.List;

import br.elibrary.model.Book;
import br.elibrary.model.Copy;
import br.elibrary.model.enuns.CopyStatus;
import br.elibrary.service.BookService;
import br.elibrary.service.CatalogStatusService;
import jakarta.ejb.EJB;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Stateless
@Remote(BookService.class)
public class BookSB implements BookService {

	@PersistenceContext(unitName = "E-Library")
	private EntityManager em;

	@EJB
	private CatalogStatusService catalogStatusSB;

	@Override
	public Book create(Book book) {
		em.persist(book);
		catalogStatusSB.onBookCreated();
		return book;
	}

	@Override
	public Book update(Book book) {
		em.merge(book);
		return book;
	}

	@Override
	public void delete(Book book) {

		Book managed = em.find(Book.class, book.getId());
		
		if (managed == null)
			return;

		long totalCopies = managed.getCopies().size();
		long availableCopies = managed.getCopies().stream().filter(c -> c.getStatus() == CopyStatus.AVAILABLE).count();

		em.remove(managed);

		catalogStatusSB.onBookDeleted((int) totalCopies, (int) availableCopies);
	}

	@Override
	public Book findById(Long id) {
		return em.find(Book.class, id);
	}

	@Override
	public List<Book> findAll() {
		return em.createQuery("SELECT b FROM Book b ORDER BY b.title", Book.class).getResultList();
	}

	@Override
	public List<Book> findByTitle(String title) {
		TypedQuery<Book> query = em.createQuery(
				"SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(:title) ORDER BY b.title", Book.class);

		query.setParameter("title", "%" + title + "%");

		return query.getResultList();
	}

	public List<Object[]> findBooksWithCopyStats() {

		String jpql = """
				SELECT b,
				       COUNT(c) AS totalCopies,
				       SUM(CASE WHEN c.status = :available THEN 1 ELSE 0 END) AS availableCopies
				FROM Book b
				LEFT JOIN b.copies c
				GROUP BY b.id
				ORDER BY b.title
				""";

		return em.createQuery(jpql, Object[].class).setParameter("available", CopyStatus.AVAILABLE).getResultList();
	}

	@Override
	public Copy findFirstAvailableCopy(Long bookId) {
		return em.createQuery("SELECT c FROM Copy c WHERE c.book.id = :bookId AND c.status = :status", Copy.class)
				.setParameter("bookId", bookId).setParameter("status", CopyStatus.AVAILABLE).setMaxResults(1)
				.getResultStream().findFirst().orElse(null);
	}
}