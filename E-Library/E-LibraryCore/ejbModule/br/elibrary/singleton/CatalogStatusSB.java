package br.elibrary.singleton;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import br.elibrary.dto.DashboardDTO;
import br.elibrary.model.enuns.CopyStatus;
import br.elibrary.service.CatalogStatusService;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.ConcurrencyManagement;
import jakarta.ejb.ConcurrencyManagementType;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Remote;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Singleton responsável por manter em memória os contadores globais da
 * biblioteca. Utiliza AtomicInteger para garantir thread-safety e
 * ConcurrencyManagement do Container.
 */
@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Remote(CatalogStatusService.class)
public class CatalogStatusSB implements CatalogStatusService {

	@PersistenceContext(unitName = "E-Library")
	private EntityManager em;

	private final AtomicInteger totalBooks = new AtomicInteger(0);
	private final AtomicInteger totalCopies = new AtomicInteger(0);
	private final AtomicInteger availableCopies = new AtomicInteger(0);
	private final AtomicInteger reservedCopies = new AtomicInteger(0);
	private final AtomicInteger borrowedCopies = new AtomicInteger(0);

	/**
	 * Inicializa o cache assim que o servidor WildFly sobe.
	 */
	@PostConstruct
	public void init() {
		refreshCache();
	}

	/**
	 * Retorna o DTO completo para o Dashboard de uma única vez. LockType.READ
	 * 
	 * permite que múltiplos usuários consultem simultaneamente.
	 */
	@Override
	@Lock(LockType.READ)
	public DashboardDTO getFullDashboard() {
		return new DashboardDTO(totalBooks.get(), totalCopies.get(), availableCopies.get(), reservedCopies.get(),
				borrowedCopies.get());
	}

	/**
	 * Sincroniza os contadores com o estado atual do banco de dados. Utilizado no
	 * 
	 * startup ou quando houver inconsistência detectada.
	 */
	@Override
	@Lock(LockType.WRITE)
	public void refreshCache() {
		
		Long bookCount = em.createQuery("SELECT COUNT(b) FROM Book b", Long.class).getSingleResult();
		totalBooks.set(bookCount.intValue());

		Long copyCount = em.createQuery("SELECT COUNT(c) FROM Copy c", Long.class).getSingleResult();
		totalCopies.set(copyCount.intValue());

		List<Object[]> results = em.createQuery("SELECT c.status, COUNT(c) FROM Copy c GROUP BY c.status", Object[].class).getResultList();

		availableCopies.set(0);
		reservedCopies.set(0);
		borrowedCopies.set(0);

		for (Object[] result : results) {
			CopyStatus status = (CopyStatus) result[0];
			int count = ((Long) result[1]).intValue();

			updateAtomicValue(status, count, false); // false = set direto
		}
	}

	/**
	 * Incrementa contadores quando um novo exemplar é inserido. Assume-se que um
	 * exemplar novo nasce como AVAILABLE.
	 */
	@Override
	@Lock(LockType.WRITE)
	public void onCopyCreated() {
		totalCopies.incrementAndGet();
		availableCopies.incrementAndGet();
	}

	/**
	 * Gerencia a transição de status de um exemplar (ex: de DISPONÍVEL para
	 * EMPRESTADO).
	 */
	@Override
	@Lock(LockType.WRITE)
	public void onCopyStatusChanged(CopyStatus oldStatus, CopyStatus newStatus) {
		
		if (oldStatus != null) {
			updateAtomicValue(oldStatus, -1, true);
		}
		
		if (newStatus != null) {
			updateAtomicValue(newStatus, 1, true); 
		}
	}

	/**
	 * Incrementa o contador de livros únicos.
	 */
	@Override
	@Lock(LockType.WRITE)
	public void onBookCreated() {
		totalBooks.incrementAndGet();
	}

	/**
	 * Decrementa o total de exemplares.
	 */
	@Override
	@Lock(LockType.WRITE)
	public void onCopyDeleted() {
		totalCopies.decrementAndGet();
	}

	/**
	 * Decrementa o total e o contador específico do status do exemplar deletado.
	 */
	@Override
	@Lock(LockType.WRITE)
	public void onCopyDeleted(CopyStatus status) {
		totalCopies.decrementAndGet();
		updateAtomicValue(status, -1, true);
	}

	/**
	 * Atualiza os contadores quando um livro (e seus exemplares) são removidos em
	 * massa.
	 */
	@Override
	@Lock(LockType.WRITE)
	public void onBookDeleted(int numCopies, int numAvailable) {
		totalBooks.decrementAndGet();
		this.totalCopies.addAndGet(-numCopies);
		this.availableCopies.addAndGet(-numAvailable);
	}

	/**
	 * Método auxiliar privado para manipular os AtomicIntegers de forma
	 * centralizada.
	 * 
	 * @param isDelta Se true, soma o valor. Se false, define o valor (set).
	 */
	private void updateAtomicValue(CopyStatus status, int value, boolean isDelta) {
		if (status == null)
			return;

		switch (status) {
		case AVAILABLE:
			if (isDelta)
				availableCopies.addAndGet(value);
			else
				availableCopies.set(value);
			break;
		case RESERVED:
			if (isDelta)
				reservedCopies.addAndGet(value);
			else
				reservedCopies.set(value);
			break;
		case BORROWED:
			if (isDelta)
				borrowedCopies.addAndGet(value);
			else
				borrowedCopies.set(value);
			break;
		}
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
}