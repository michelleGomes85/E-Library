package br.elibrary.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * Classe Livros
 */
@Entity
@Table(name = "books")
public class Book {
	
	@Id
	@SequenceGenerator(
			name="book_id", 
			sequenceName="book_seq",
			allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="book_id")
    private Long id;
	
	@Column(unique = true)
    private String isbn;
	
	@Column(nullable = false)
    private String title;
	
	@Column(nullable = false)
    private String author;	
    
	@Column(nullable = false)
    private String publisher;
    
	@Column(nullable = false)
    private Integer year;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Copy> copies = new HashSet<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
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

	public Set<Copy> getCopies() {
		return copies;
	}

	public void setCopies(Set<Copy> copies) {
		this.copies = copies;
	}
}
