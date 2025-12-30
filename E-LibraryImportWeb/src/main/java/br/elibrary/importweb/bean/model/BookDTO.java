package br.elibrary.importweb.bean.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "livro")
public class BookDTO {

	private Long id;

	@JsonProperty("isbn")
	@JacksonXmlProperty(localName = "isbn")
	private String isbn;

	@JsonProperty("titulo")
	@JacksonXmlProperty(localName = "titulo")
	private String title;

	@JsonProperty("autor")
	@JacksonXmlProperty(localName = "autor")
	private String author;

	@JsonProperty("editora")
	@JacksonXmlProperty(localName = "editora")
	private String publisher;

	@JsonProperty("anoPublicacao")
	@JacksonXmlProperty(localName = "anoPublicacao")
	private Integer year;

	@JsonProperty("quantidadeExemplares")
	@JacksonXmlProperty(localName = "quantidadeExemplares")
	private Integer quantity; 

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

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
}