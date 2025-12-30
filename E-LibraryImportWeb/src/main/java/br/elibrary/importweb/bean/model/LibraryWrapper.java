package br.elibrary.importweb.bean.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.List;

@JacksonXmlRootElement(localName = "biblioteca")
public class LibraryWrapper {

	@JsonProperty("livros")
	@JacksonXmlProperty(localName = "livro")
	@JacksonXmlElementWrapper(useWrapping = false)
	private List<BookDTO> books;

	public List<BookDTO> getBooks() {
		return books;
	}

	public void setBooks(List<BookDTO> books) {
		this.books = books;
	}
}