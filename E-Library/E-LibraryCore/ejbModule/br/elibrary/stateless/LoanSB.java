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

/**
 * Session Bean Stateless responsável pela consulta e gestão de registros de
 * Empréstimos.
 */
@Stateless
public class LoanSB implements LoanService {

	@PersistenceContext(unitName = "E-Library")
	private EntityManager em;

	/**
	 * Busca todos os exemplares (objetos Copy) que estão atualmente com o usuário.
	 * 
	 * @param userId ID do usuário logado.
	 * @return Lista de exemplares cujo empréstimo associado está ATIVO.
	 */
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
		return em.createQuery(jpql, Copy.class).setParameter("userId", userId)
				.setParameter("activeStatus", LoanStatus.ACTIVE).getResultList();
	}

	/**
	 * Retorna uma lista de livros que não possuem nenhuma cópia disponível no
	 * momento.
	 * 
	 * Utilizado para alimentar a lista de "Indisponíveis" ou para listas de espera.
	 */
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

		return em.createQuery(jpql, Book.class).setParameter("available", CopyStatus.AVAILABLE).getResultList();
	}

	/**
	 * Recupera os registros completos de Empréstimo (Loan) de um usuário.
	 * 
	 * Diferente do findBorrowedCopies, este retorna as datas e prazos (objeto
	 * Loan).
	 */
	@Override
	public List<Loan> findActiveLoansByUser(Long userId) {

		if (userId == null) {
			return List.of();
		}

		return em.createQuery("SELECT l FROM Loan l WHERE l.user.id = :userId AND l.status = :status", Loan.class)
				.setParameter("userId", userId).setParameter("status", LoanStatus.ACTIVE).getResultList();
	}

	/**
	 * Localiza o registro de empréstimo ativo associado a uma cópia específica.
	 * 
	 * Essencial para o processo de devolução:
	 * 
	 * a partir da cópia, encontra-se o contrato de empréstimo.
	 */
	@Override
	public Loan findActiveLoanByCopyId(Long copyId) {

		if (copyId == null)
			return null;

		return em.createQuery("SELECT l FROM Loan l WHERE l.copy.id = :copyId AND l.status = :status", Loan.class)
				.setParameter("copyId", copyId).setParameter("status", LoanStatus.ACTIVE).getResultStream().findFirst()
				.orElse(null);
	}

	/**
	 * Verifica se uma cópia específica possui um empréstimo com status ACTIVE. Este
	 * método é vital para impedir que o administrador altere exemplares que estão
	 * em posse de usuários. * @param copyId ID da cópia a ser verificada.
	 * 
	 * @return true se houver empréstimo ativo, false caso contrário.
	 */
	@Override
	public boolean hasActiveLoan(Long copyId) {

		if (copyId == null)
			return false;

		Long count = em
				.createQuery("SELECT COUNT(l) FROM Loan l WHERE l.copy.id = :copyId AND l.status = :status", Long.class)
				.setParameter("copyId", copyId).setParameter("status", LoanStatus.ACTIVE).getSingleResult();

		return count > 0;
	}
}