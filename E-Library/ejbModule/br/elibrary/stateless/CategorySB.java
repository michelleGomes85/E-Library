package br.elibrary.stateless;

import java.util.List;

import br.elibrary.model.Category;
import br.elibrary.service.CategoryService;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Stateless
public class CategorySB implements CategoryService {

	@PersistenceContext
	private EntityManager em;

	@Override
	public Category create(Category category) {
		em.persist(category);
		return category;
	}

	@Override
	public Category update(Category category) {
		return em.merge(category);
	}

	@Override
	public void delete(Category category) {
		if (!em.contains(category)) {
			category = em.merge(category);
		}
		em.remove(category);
	}

	@Override
	public Category findById(Long id) {
		return em.find(Category.class, id);
	}

	@Override
	public List<Category> findAll() {
		TypedQuery<Category> query = em.createQuery("SELECT c FROM Category c ORDER BY c.name", Category.class);
		return query.getResultList();
	}
}
