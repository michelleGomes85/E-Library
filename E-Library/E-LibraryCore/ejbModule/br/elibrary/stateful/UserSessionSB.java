package br.elibrary.stateful;

import java.time.LocalDate;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

import br.elibrary.dto.LoanDTO;
import br.elibrary.dto.UserDTO;
import br.elibrary.mapper.UserMapper;
import br.elibrary.model.Copy;
import br.elibrary.model.Loan;
import br.elibrary.model.User;
import br.elibrary.model.enuns.CopyStatus;
import br.elibrary.model.enuns.LoanStatus;
import br.elibrary.service.CatalogStatusService;
import br.elibrary.service.LoanService;
import br.elibrary.service.UserSessionService;
import br.elibrary.service.internal.LoanInternalService;
import jakarta.ejb.EJB;
import jakarta.ejb.Remove;
import jakarta.ejb.Stateful;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateful
public class UserSessionSB implements UserSessionService {

	@PersistenceContext(unitName = "E-Library")
	private EntityManager em;

	private User currentUser;

	@EJB
	private CatalogStatusService catalogStatusSB;

	@EJB
	private LoanService loanSB;
	
	@EJB
	private LoanInternalService loanSBInternal;
	
	@Override
	public boolean login(String registration, String passwordPlain) {

		User user = em.createQuery("SELECT u FROM User u WHERE u.registration = :reg", User.class)
				.setParameter("reg", registration).getResultStream().findFirst().orElse(null);

		if (user == null)
			return false;

		boolean passwordOk = BCrypt.checkpw(passwordPlain, user.getPasswordHash());

		if (passwordOk) {
			currentUser = user;
			return true;
		}

		return false;
	}

	@Override
	public UserDTO getLoggedInUser() {
		return currentUser != null ? UserMapper.toDTO(currentUser) : null;
	}

	@Override
	@Remove
	public void logout() {
		this.currentUser = null;
	}

	@Override
	public boolean borrowCopy(Long copyId) {

		if (currentUser == null)
			throw new IllegalStateException("Usuário não autenticado.");

		Copy copy = em.find(Copy.class, copyId);

		if (copy == null || copy.getStatus() != CopyStatus.RESERVED)
			return false;

		Loan loan = new Loan();
		loan.setUser(currentUser);
		loan.setCopy(copy);
		loan.setIssueDate(LocalDate.now());
		loan.setDueDate(LocalDate.now().plusDays(14));
		loan.setStatus(LoanStatus.ACTIVE);
		loan.setReturnDate(null);

		em.persist(loan);

		copy.setStatus(CopyStatus.BORROWED);

		catalogStatusSB.onCopyStatusChanged(CopyStatus.RESERVED, CopyStatus.BORROWED);

		return true;
	}

	@Override
	public boolean returnCopy(Long copyId) {

		if (currentUser == null)
			throw new IllegalStateException("Usuário não autenticado.");

		Copy copy = em.find(Copy.class, copyId);

		if (copy == null || copy.getStatus() != CopyStatus.BORROWED) {
			return false;
		}

		Loan activeLoan = loanSBInternal.findActiveLoanByCopyIdEntity(copyId);
		
		if (activeLoan == null) {
			copy.setStatus(CopyStatus.AVAILABLE);
			return false;
		}

		activeLoan.setReturnDate(LocalDate.now());
		activeLoan.setStatus(LoanStatus.RETURNED);
		copy.setStatus(CopyStatus.AVAILABLE);
		
		em.flush();
		em.refresh(copy);

		catalogStatusSB.onCopyStatusChanged(CopyStatus.BORROWED, CopyStatus.AVAILABLE);

		return true;
	}

	@Override
	public List<LoanDTO> getActiveLoans() {
		
		if (currentUser == null)
			return List.of();

		List<LoanDTO> loans = loanSB.findActiveLoansByUser(currentUser.getId());
		
		return loans;
	}
}