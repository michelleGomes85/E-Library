package br.elibrary.model;

import br.elibrary.model.enuns.CopyStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * Classe Exemplar de um livro
 */
@Entity
@Table(name = "copies")
public class Copy {
	
	@Id
	@SequenceGenerator(
			name="copy_id", 
			sequenceName="copy_seq",
			allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="copy_id")
	private Long id;
	
	@Column(nullable = false, unique = true)
	private String internalCode;

	@ManyToOne
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CopyStatus status = CopyStatus.AVAILABLE;

	@OneToOne(mappedBy = "copy", cascade = CascadeType.ALL)
	private Loan loan;

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

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public CopyStatus getStatus() {
		return status;
	}

	public void setStatus(CopyStatus status) {
		this.status = status;
	}

	public Loan getLoan() {
		return loan;
	}

	public void setLoan(Loan loan) {
		this.loan = loan;
	}
}