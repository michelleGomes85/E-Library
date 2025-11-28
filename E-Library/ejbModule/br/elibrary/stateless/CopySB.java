package br.elibrary.stateless;

import java.util.List;

import br.elibrary.model.Copy;
import br.elibrary.model.enuns.CopyStatus;
import br.elibrary.service.CatalogStatusService;
import br.elibrary.service.CopyService;
import jakarta.ejb.EJB;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
@Remote(CopyService.class)
public class CopySB implements CopyService {
	
    @PersistenceContext(unitName = "E-Library")
    private EntityManager em;
    
    @EJB
    private CatalogStatusService catalogStatusSB;

	@Override
	public Copy create(Copy copy) {
		em.persist(copy);
		catalogStatusSB.onCopyCreated();
		return copy;
	}

	@Override
	public Copy update(Copy copy) {
		em.merge(copy);
		return copy;
	}

	@Override
	public void delete(Copy copy) {
		
	    Copy managed = em.merge(copy);
	    CopyStatus status = managed.getStatus();
	    em.remove(managed);
	    
	    if (status == CopyStatus.AVAILABLE)
	        catalogStatusSB.onCopyStatusChanged(CopyStatus.AVAILABLE, null);
	    else
	        catalogStatusSB.onCopyStatusChanged(status, null);
	    
	    catalogStatusSB.onCopyDeleted();
	}
	
	@Override
	public void deleteById(Long id) {
		
	    Copy copy = em.find(Copy.class, id);
	    
	    if (copy != null) {
	        CopyStatus status = copy.getStatus();
	        em.remove(copy);
	        if (status == CopyStatus.AVAILABLE)
	            catalogStatusSB.onCopyStatusChanged(CopyStatus.AVAILABLE, null);
	        
	        catalogStatusSB.onCopyDeleted();
	    }
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
    public List<Copy> findAvailableCopiesByBookId(Long bookId) {
    	
        return em.createQuery("""
            SELECT c FROM Copy c
            WHERE c.book.id = :bookId
              AND c.status = br.elibrary.model.CopyStatus.AVAILABLE
            """, Copy.class)
            .setParameter("bookId", bookId)
            .getResultList();
    }
}
