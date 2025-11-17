package br.elibrary.model.service;

import java.util.List;

import br.elibrary.model.Book;

public interface BookService {
	
    Book create(Book book);
    
    Book update(Book book);
    
    void delete(Book book);
    
    Book findById(Long id);
    List<Book> findAll();
    List<Book> findByTitle(String title);
}
