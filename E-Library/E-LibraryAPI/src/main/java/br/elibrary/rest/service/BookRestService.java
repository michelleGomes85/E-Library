package br.elibrary.rest.service;

import java.util.List;

import br.elibrary.dto.BookDTO;
import br.elibrary.service.BookService;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BookRestService {

    @EJB
    private BookService bookSB;

    public BookDTO create(BookDTO dto) {
        return bookSB.create(dto);
    }

    public BookDTO findById(Long id) {
        return bookSB.findById(id);
    }
    
    public List<BookDTO> findAll(String author, String category) {
        return bookSB.findByAuthorOrCategory(author, category);
    }

    public BookDTO update(Long id, BookDTO dto) {
        dto.setId(id);
        return bookSB.update(dto);
    }
}
