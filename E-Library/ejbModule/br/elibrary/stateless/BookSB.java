package br.elibrary.stateless;

import java.util.ArrayList;
import java.util.List;

import br.elibrary.model.Book;
import br.elibrary.model.Category;
import br.elibrary.model.Copy;
import br.elibrary.model.enuns.CopyStatus;
import br.elibrary.service.BookService;
import br.elibrary.service.CatalogStatusService;
import jakarta.ejb.EJB;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

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
        if (book.getCategories() != null) {
            List<Category> managedCategories = new ArrayList<>();
            for (Category c : book.getCategories()) {
                Category managed = em.find(Category.class, c.getId());
                managedCategories.add(managed);
            }
            book.setCategories(managedCategories);
        }
        return em.merge(book);
    }

    @Override
    public void delete(Book book) {
        Book managed = em.find(Book.class, book.getId());
        if (managed == null) return;

        long totalCopies = managed.getCopies().size();
        long availableCopies = managed.getCopies().stream()
            .filter(c -> c.getStatus() == CopyStatus.AVAILABLE).count();

        em.remove(managed);
        catalogStatusSB.onBookDeleted((int) totalCopies, (int) availableCopies);
    }

    @Override
    public Book findById(Long id) {
        return em.createQuery("""
            SELECT b FROM Book b 
            LEFT JOIN FETCH b.categories 
            WHERE b.id = :id
            """, Book.class)
            .setParameter("id", id)
            .getSingleResult();
    }

    @Override
    public List<Book> findAll() {
        return em.createQuery("""
            SELECT DISTINCT b FROM Book b 
            LEFT JOIN FETCH b.categories 
            ORDER BY b.title
            """, Book.class)
            .getResultList();
    }

    @Override
    public List<Object[]> findByTitleOrAuthorWithStats(String query) {
        
    	String q = "%" + query.toLowerCase() + "%";
        
        return em.createQuery("""
            SELECT b,
                   COUNT(c) AS totalCopies,
                   SUM(CASE WHEN c.status = :available THEN 1 ELSE 0 END) AS availableCopies
            FROM Book b
            LEFT JOIN b.copies c
            WHERE LOWER(b.title) LIKE :q OR LOWER(b.author) LIKE :q
            GROUP BY b.id, b.title
            ORDER BY b.title ASC
            """, Object[].class)
            .setParameter("q", q)
            .setParameter("available", CopyStatus.AVAILABLE)
            .getResultList();
    }

    @Override
    public List<Object[]> findUnavailableBooksWithStats() {
        return em.createQuery("""
            SELECT b, 
                   COUNT(c) AS totalCopies,
                   0L AS availableCopies
            FROM Book b
            INNER JOIN b.copies c
            GROUP BY b.id, b.title
            HAVING SUM(CASE WHEN c.status = :available THEN 1 ELSE 0 END) = 0
            ORDER BY b.title
            """, Object[].class)
            .setParameter("available", CopyStatus.AVAILABLE)
            .getResultList();
    }

    @Override
    public List<Object[]> findBooksWithCopyStats() {
        return em.createQuery("""
            SELECT b,
                   COUNT(c) AS totalCopies,
                   SUM(CASE WHEN c.status = :available THEN 1 ELSE 0 END) AS availableCopies
            FROM Book b
            LEFT JOIN b.copies c
            GROUP BY b.id, b.title
            ORDER BY b.title ASC
            """, Object[].class)
            .setParameter("available", CopyStatus.AVAILABLE)
            .getResultList();
    }

    @Override
    public Copy findFirstAvailableCopy(Long bookId) {
        return em.createQuery("""
            SELECT c FROM Copy c 
            WHERE c.book.id = :bookId AND c.status = :status
            """, Copy.class)
            .setParameter("bookId", bookId)
            .setParameter("status", CopyStatus.AVAILABLE)
            .setMaxResults(1)
            .getResultStream()
            .findFirst()
            .orElse(null);
    }
}