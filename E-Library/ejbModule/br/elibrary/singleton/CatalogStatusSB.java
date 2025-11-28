package br.elibrary.singleton;

import java.util.concurrent.atomic.AtomicInteger;

import br.elibrary.model.enuns.CopyStatus;
import br.elibrary.service.CatalogStatusService;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.ConcurrencyManagement;
import jakarta.ejb.ConcurrencyManagementType;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class CatalogStatusSB implements CatalogStatusService {

    @PersistenceContext
    private EntityManager em;

    private final AtomicInteger totalBooks = new AtomicInteger(0);
    private final AtomicInteger totalCopies = new AtomicInteger(0);
    private final AtomicInteger availableCopies = new AtomicInteger(0);

    @PostConstruct
    public void init() {
        refreshCache();
    }

    @Override
    @Lock(LockType.READ)
    public int getTotalBooks() {
        return totalBooks.get();
    }

    @Override
    @Lock(LockType.READ)
    public int getTotalCopies() {
        return totalCopies.get();
    }

    @Override
    @Lock(LockType.READ)
    public int getAvailableCopies() {
        return availableCopies.get();
    }

    @Override
    @Lock(LockType.WRITE)
    public void refreshCache() {
        Long bookCount = em.createQuery("SELECT COUNT(b) FROM Book b", Long.class)
                           .getSingleResult();

        Long copyCount = em.createQuery("SELECT COUNT(c) FROM Copy c", Long.class)
                           .getSingleResult();

        Long availableCount = em.createQuery(
                "SELECT COUNT(c) FROM Copy c WHERE c.status = :status", Long.class)
                .setParameter("status", CopyStatus.AVAILABLE)
                .getSingleResult();

        totalBooks.set(bookCount.intValue());
        totalCopies.set(copyCount.intValue());
        availableCopies.set(availableCount.intValue());
    }

    @Lock(LockType.WRITE)
    public void onCopyCreated() {
        totalCopies.incrementAndGet();
        availableCopies.incrementAndGet();
    }

    @Lock(LockType.WRITE)
    public void onCopyStatusChanged(CopyStatus oldStatus, CopyStatus newStatus) {
    	
        if (oldStatus == CopyStatus.AVAILABLE && newStatus != CopyStatus.AVAILABLE) {
            availableCopies.decrementAndGet();
        } else if (oldStatus != CopyStatus.AVAILABLE && newStatus == CopyStatus.AVAILABLE) {
            availableCopies.incrementAndGet();
        }
    }

    @Lock(LockType.WRITE)
    public void onBookCreated() {
        totalBooks.incrementAndGet();
    }
    
    @Lock(LockType.WRITE)
    public void onCopyDeleted() {
        totalCopies.decrementAndGet();
    }
}