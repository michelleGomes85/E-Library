package br.elibrary.model;

import java.io.Serializable;
import java.time.LocalDate;

import br.elibrary.model.enuns.LoanStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "loans")
public class Loan implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "loan_id", sequenceName = "loan_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loan_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name = "copy_id", nullable = false)
	private Copy copy;

	// Data do empréstimo
	@Column(name = "issue_date", nullable = false)
	private LocalDate issueDate;

	// Data limite para devolução
	@Column(name = "due_date", nullable = false)
	private LocalDate dueDate;

	// Data da devolução
	@Column(name = "return_date")
	private LocalDate returnDate;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private LoanStatus status = LoanStatus.ACTIVE;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Copy getCopy() {
		return copy;
	}

	public void setCopy(Copy copy) {
		this.copy = copy;
	}

	public LocalDate getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(LocalDate issueDate) {
		this.issueDate = issueDate;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public LocalDate getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(LocalDate returnDate) {
		this.returnDate = returnDate;
	}

	public LoanStatus getStatus() {
		return status;
	}

	public void setStatus(LoanStatus status) {
		this.status = status;
	}

	public LocalDate getBorrowDate() {
		return issueDate;
	}

	public long getDaysRemaining() {
		if (dueDate == null)
			return 0;
		return LocalDate.now().until(dueDate).getDays();
	}

	public boolean isOverdue() {
		return dueDate != null && dueDate.isBefore(LocalDate.now());
	}
}
