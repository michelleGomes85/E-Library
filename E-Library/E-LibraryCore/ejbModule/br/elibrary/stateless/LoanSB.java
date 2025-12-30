package br.elibrary.stateless;

import java.util.List;
import java.util.stream.Collectors;

import br.elibrary.dto.BookDTO;
import br.elibrary.dto.CopyDTO;
import br.elibrary.dto.LoanDTO;
import br.elibrary.mapper.BookMapper;
import br.elibrary.mapper.CopyMapper;
import br.elibrary.mapper.LoanMapper;
import br.elibrary.model.Book;
import br.elibrary.model.Copy;
import br.elibrary.model.Loan;
import br.elibrary.model.enuns.CopyStatus;
import br.elibrary.model.enuns.LoanStatus;
import br.elibrary.service.LoanService;
import br.elibrary.service.internal.LoanInternalService;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Session Bean Stateless responsável pela consulta e gestão de registros de
 * Empréstimos.
 */
@Stateless
public class LoanSB implements LoanService, LoanInternalService {

    @PersistenceContext(unitName = "E-Library")
    private EntityManager em;

    /**
     * Busca todos os exemplares (Copy) atualmente emprestados a um usuário.
     */
    @Override
    public List<CopyDTO> findBorrowedCopiesByUser(Long userId) {

        if (userId == null) {
            return List.of();
        }

        String jpql = """
            SELECT l.copy
            FROM Loan l
            JOIN FETCH l.copy
            JOIN FETCH l.copy.book
            WHERE l.user.id = :userId
              AND l.status = :activeStatus
        """;

        List<Copy> copies = em.createQuery(jpql, Copy.class)
                .setParameter("userId", userId)
                .setParameter("activeStatus", LoanStatus.ACTIVE)
                .getResultList();

        return copies.stream()
                .map(CopyMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retorna uma lista de livros que não possuem nenhuma cópia disponível.
     */
    @Override
    public List<BookDTO> findBooksWithNoAvailableCopies() {

        String jpql = """
            SELECT DISTINCT b
            FROM Book b
            LEFT JOIN FETCH b.copies
            WHERE (
                SELECT COUNT(c)
                FROM Copy c
                WHERE c.book = b
                  AND c.status = :available
            ) = 0
            ORDER BY b.title
        """;

        List<Book> books = em.createQuery(jpql, Book.class)
                .setParameter("available", CopyStatus.AVAILABLE)
                .getResultList();

        return books.stream()
                .map(BookMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Recupera os empréstimos ativos de um usuário.
     */
    @Override
    public List<LoanDTO> findActiveLoansByUser(Long userId) {

        if (userId == null) {
            return List.of();
        }

        String jpql = """
            SELECT l
            FROM Loan l
            JOIN FETCH l.copy
            JOIN FETCH l.copy.book
            WHERE l.user.id = :userId
              AND l.status = :status
        """;

        List<Loan> loans = em.createQuery(jpql, Loan.class)
                .setParameter("userId", userId)
                .setParameter("status", LoanStatus.ACTIVE)
                .getResultList();

        return loans.stream()
                .map(LoanMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Localiza o empréstimo ativo associado a uma cópia específica.
     */
    @Override
    public LoanDTO findActiveLoanByCopyId(Long copyId) {
    	return LoanMapper.toDTO(findActiveLoanByCopyIdEntity(copyId));
    }
    
    @Override
    public Loan findActiveLoanByCopyIdEntity(Long copyId) {

        if (copyId == null) {
            return null;
        }

        String jpql = """
            SELECT l
            FROM Loan l
            JOIN FETCH l.copy
            JOIN FETCH l.copy.book
            WHERE l.copy.id = :copyId
              AND l.status = :status
        """;

        return em.createQuery(jpql, Loan.class)
                .setParameter("copyId", copyId)
                .setParameter("status", LoanStatus.ACTIVE)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    /**
     * Verifica se uma cópia possui empréstimo ativo.
     */
    @Override
    public boolean hasActiveLoan(Long copyId) {

        if (copyId == null) {
            return false;
        }

        Long count = em.createQuery(
                "SELECT COUNT(l) FROM Loan l WHERE l.copy.id = :copyId AND l.status = :status",
                Long.class)
            .setParameter("copyId", copyId)
            .setParameter("status", LoanStatus.ACTIVE)
            .getSingleResult();

        return count > 0;
    }
}