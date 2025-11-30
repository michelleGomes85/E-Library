package br.elibrary.stateless;

import java.util.List;

import br.elibrary.model.Book;
import br.elibrary.model.Copy;
import br.elibrary.model.Loan;
import br.elibrary.model.enuns.CopyStatus;
import br.elibrary.model.enuns.LoanStatus;
import br.elibrary.service.LoanService;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class LoanSB implements LoanService {

    @PersistenceContext(unitName = "E-Library")
    private EntityManager em;
    
    @Override
    public List<Copy> findBorrowedCopiesByUser(Long userId) {
    	
        if (userId == null) {
            return List.of();
        }
        String jpql = """
            SELECT l.copy
            FROM Loan l
            WHERE l.user.id = :userId
              AND l.status = :activeStatus
            """;
        return em.createQuery(jpql, Copy.class)
                 .setParameter("userId", userId)
                 .setParameter("activeStatus", LoanStatus.ACTIVE)
                 .getResultList();
    }
    
    @Override
    public List<Book> findBooksWithNoAvailableCopies() {
        String jpql = """
            SELECT b
            FROM Book b
            WHERE (
                SELECT COUNT(c)
                FROM Copy c
                WHERE c.book = b AND c.status = :available
            ) = 0
            ORDER BY b.title
            """;
        
        return em.createQuery(jpql, Book.class)
        		  .setParameter("available", CopyStatus.AVAILABLE)
                 .getResultList();
    }
    
    @Override
    public List<Loan> findActiveLoansByUser(Long userId) {
        
    	if (userId == null) {
            return List.of();
        }
        
        return em.createQuery(
            "SELECT l FROM Loan l WHERE l.user.id = :userId AND l.status = :status", Loan.class)
            .setParameter("userId", userId)
            .setParameter("status", LoanStatus.ACTIVE)
            .getResultList();
    }
    
    @Override
    public Loan findActiveLoanByCopyId(Long copyId) {
    	
        if (copyId == null)
            return null;
        
        return em.createQuery(
                "SELECT l FROM Loan l WHERE l.copy.id = :copyId AND l.status = :status", Loan.class)
                .setParameter("copyId", copyId)
                .setParameter("status", LoanStatus.ACTIVE)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }
}