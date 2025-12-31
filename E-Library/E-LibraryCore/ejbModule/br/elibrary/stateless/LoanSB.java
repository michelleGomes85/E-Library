package br.elibrary.stateless;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
import br.elibrary.service.NotificationPublisher;
import br.elibrary.service.internal.LoanInternalService;
import br.elibrary.service.internal.WaitingInternalListService;
import jakarta.ejb.EJB;
import jakarta.ejb.Schedule;
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

	@EJB
	private WaitingInternalListService waitingListService;

	@EJB
	private NotificationPublisher notificationPublisher;

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

		List<Copy> copies = em.createQuery(jpql, Copy.class).setParameter("userId", userId)
				.setParameter("activeStatus", LoanStatus.ACTIVE).getResultList();

		return copies.stream().map(CopyMapper::toDTO).collect(Collectors.toList());
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

		List<Book> books = em.createQuery(jpql, Book.class).setParameter("available", CopyStatus.AVAILABLE)
				.getResultList();

		return books.stream().map(BookMapper::toDTO).collect(Collectors.toList());
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

		List<Loan> loans = em.createQuery(jpql, Loan.class).setParameter("userId", userId)
				.setParameter("status", LoanStatus.ACTIVE).getResultList();

		notifyLoanDelay(loans);

		return loans.stream().map(LoanMapper::toDTO).collect(Collectors.toList());
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

		return em.createQuery(jpql, Loan.class).setParameter("copyId", copyId).setParameter("status", LoanStatus.ACTIVE)
				.getResultStream().findFirst().orElse(null);
	}

	/**
	 * Verifica se uma cópia possui empréstimo ativo.
	 */
	@Override
	public boolean hasActiveLoan(Long copyId) {

		if (copyId == null) {
			return false;
		}

		Long count = em
				.createQuery("SELECT COUNT(l) FROM Loan l WHERE l.copy.id = :copyId AND l.status = :status", Long.class)
				.setParameter("copyId", copyId).setParameter("status", LoanStatus.ACTIVE).getSingleResult();

		return count > 0;
	}

	/**
	 * Este método será executado automaticamente pelo servidor. second, minute,
	 * hour, dayOfMonth, month, dayOfWeek, year
	 */
	@Schedule(hour = "0", minute = "0", second = "0", persistent = false)
	public void verifyDelayAutomatic() {
		System.out.println("LOG: Iniciando varredura automática de atrasos: " + LocalDateTime.now());
		this.verifyDelayFull();
	}

	private void verifyDelayFull() {

		String jpql = """
				    SELECT l
				    FROM Loan l
				    JOIN FETCH l.user
				    JOIN FETCH l.copy
				    WHERE l.status = :status
				      AND l.dueDate < :today
				""";

		List<Loan> allOverdue = em.createQuery(jpql, Loan.class).setParameter("status", LoanStatus.ACTIVE)
				.setParameter("today", LocalDate.now()).getResultList();

		notifyLoanDelay(allOverdue);
	}

	private void notifyLoanDelay(List<Loan> loans) {

		LocalDate today = LocalDate.now();
		
		for (Loan loan : loans) {
			if (loan.getDueDate().isBefore(today)) {
				long days = ChronoUnit.DAYS.between(loan.getDueDate(), LocalDate.now());
				notificationPublisher.publishLoanOverdue(loan.getUser().getId(), loan.getCopy().getId(),
						loan.getDueDate(), days);
			}
		}
	}
}