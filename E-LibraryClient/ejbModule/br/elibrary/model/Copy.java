package br.elibrary.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "copies")
public class Copy implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "copy_id", sequenceName = "copy_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "copy_id")
	private Long id;

	@Column(nullable = false, unique = true)
	private String internalCode;

	@ManyToOne
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CopyStatus status = CopyStatus.AVAILABLE;

	@OneToMany(mappedBy = "copy", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private Set<Loan> loans = new HashSet<>();

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

	public Set<Loan> getLoans() {
		return loans;
	}

	public void setLoans(Set<Loan> loans) {
		this.loans = loans;
	}
}