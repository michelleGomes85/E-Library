package br.elibrary.dto;

import java.io.Serializable;
import br.elibrary.model.enuns.CopyStatus;

public class CopyDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private Long id;
	private String internalCode;
	private CopyStatus status;

	private Long bookId;
	
	private String bookTitle;
	 private String bookIsbn;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getInternalCode() {
		return internalCode;
	}

	public void setInternalCode(String internalCode) {
		this.internalCode = internalCode;
	}

	public CopyStatus getStatus() {
		return status;
	}

	public void setStatus(CopyStatus status) {
		this.status = status;
	}

	public Long getBookId() {
		return bookId;
	}

	public void setBookId(Long bookId) {
		this.bookId = bookId;
	}

	public String getBookTitle() {
		return bookTitle;
	}

	public void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
	}

	public String getBookIsbn() {
		return bookIsbn;
	}

	public void setBookIsbn(String bookIsbn) {
		this.bookIsbn = bookIsbn;
	}
}