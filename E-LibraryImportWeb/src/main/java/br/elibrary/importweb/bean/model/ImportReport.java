package br.elibrary.importweb.bean.model;

import java.io.Serializable;

public class ImportReport implements Serializable {

	private static final long serialVersionUID = 1L;

	private String title;
	private String author;
	private String isbn;
	private String publisher;
	private Integer year;
	private Integer quantity;
	private String status;
	private String message;

	public ImportReport(String title, String author, String isbn, String publisher, Integer year, Integer quantity,
			String status, String message) {
		this.title = title;
		this.author = author;
		this.isbn = isbn;
		this.publisher = publisher;
		this.year = year;
		this.quantity = quantity;
		this.status = status;
		this.message = message;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}