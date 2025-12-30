package br.elibrary.service;

import java.util.List;

import br.elibrary.dto.BookDTO;
import br.elibrary.dto.CopyDTO;
import jakarta.ejb.Remote;

@Remote
public interface BookService {

	BookDTO create(BookDTO book);

	BookDTO update(BookDTO book);

	void delete(BookDTO book);

	BookDTO findById(Long id);
	
	BookDTO findByIsbn(String isbn);

	List<BookDTO> findAll();

	List<Object[]> findByTitleOrAuthorWithStats(String query);

	List<Object[]> findBooksWithCopyStats();

	CopyDTO findFirstAvailableCopy(Long bookId);	

	List<Object[]> findUnavailableBooksWithStats();
	
	List<BookDTO> findByAuthorOrCategory(String author, String categoryName);
	
	List<BookDTO> findAvailableBooks(String author, String status);
}
