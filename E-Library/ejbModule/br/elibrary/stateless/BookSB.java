package br.elibrary.stateless;

import java.util.List;

import br.elibrary.model.Book;
import br.elibrary.model.service.BookService;
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

	@Override
	public Book create(Book book) {
		em.persist(book);
		return book;
	}

	@Override
	public Book update(Book book) {
		em.merge(book);
		return book;
	}

	@Override
	public void delete(Book book) {
		em.remove(em.merge(book));
	}

	@Override
	public Book findById(Long id) {
		return em.find(Book.class, id);
	}

    @Override
    public List<Book> findAll() {
        return em.createQuery("SELECT b FROM Book b ORDER BY b.title", Book.class)
                  .getResultList();
    }

    @Override
    public List<Book> findByTitle(String title) {
        TypedQuery<Book> query = em.createQuery("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(:title) ORDER BY b.title", Book.class);
        
        query.setParameter("title", "%" + title + "%");
        
        return query.getResultList();
    }
}
