package br.elibrary.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import br.elibrary.model.enuns.LoanStatus;

public class LoanDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private Long copyId;
	private String copyInternalCode;
	private String bookTitle;
	private Long userId;
	private String userName;
	private LoanStatus status;
	private LocalDate issueDate;
	private LocalDate dueDate;
	private LocalDate returnDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCopyId() {
		return copyId;
	}

	public void setCopyId(Long copyId) {
		this.copyId = copyId;
	}

	public String getCopyInternalCode() {
		return copyInternalCode;
	}

	public void setCopyInternalCode(String copyInternalCode) {
		this.copyInternalCode = copyInternalCode;
	}

	public String getBookTitle() {
		return bookTitle;
	}

	public void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public LoanStatus getStatus() {
		return status;
	}

	public void setStatus(LoanStatus status) {
		this.status = status;
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

	public LocalDate getBorrowDate() {
		return issueDate;
	}

	public long getDaysRemaining() {
		if (dueDate == null)
			return 0;
		return ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
	}

	public boolean isOverdue() {
		return dueDate != null && dueDate.isBefore(LocalDate.now());
	}
}