package br.elibrary.stateless;

import java.util.List;

import br.elibrary.model.Copy;
import br.elibrary.model.enuns.CopyStatus;
import br.elibrary.model.service.CopyService;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
@Remote(CopyService.class)
public class CopySB implements CopyService {
	
    @PersistenceContext(unitName = "E-Library")
    private EntityManager em;

	@Override
	public Copy create(Copy copy) {
		em.persist(copy);
		return copy;
	}

	@Override
	public Copy update(Copy copy) {
		em.merge(copy);
		return copy;
	}

	@Override
	public void delete(Copy copy) {
		em.remove(em.merge(copy));
	}

	@Override
	public Copy findById(Long id) {
		return em.find(Copy.class, id);
	}

    @Override
    public List<Copy> findAll() {
        return em.createQuery("SELECT c FROM Copy c ORDER BY c.internalCode", Copy.class)
                  .getResultList();
    }
    
    @Override
    public List<Copy> findByBookId(Long bookId) {
        return em.createQuery("SELECT c FROM Copy c WHERE c.book.id = :bookId", Copy.class)
                  .setParameter("bookId", bookId)
                  .getResultList();
    }
    
    @Override
    public List<Copy> findByStatus(CopyStatus status) {
        return em.createQuery("SELECT c FROM Copy c WHERE c.status = :status", Copy.class)
                  .setParameter("status", status)
                  .getResultList();
    }
    
    @Override
    public List<Copy> findByInternalCode(String internalCode) {
        return em.createQuery("SELECT c FROM Copy c WHERE c.internalCode = :code", Copy.class)
                  .setParameter("code", internalCode)
                  .getResultList();
    }
	
}
