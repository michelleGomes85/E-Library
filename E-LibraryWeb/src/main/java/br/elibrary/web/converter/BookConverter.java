package br.elibrary.web.converter;

import br.elibrary.dto.BookDTO;
import br.elibrary.service.BookService;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.inject.Named;

@Named("bookConverter") 
@ApplicationScoped     
public class BookConverter implements Converter<BookDTO> {

    @EJB
    private BookService bookService;

    @Override
    public BookDTO getAsObject(FacesContext ctx, UIComponent comp, String id) {
    	
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        
        try {
            Long longId = Long.parseLong(id.trim());
            BookDTO book = bookService.findById(longId);
            return book;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext ctx, UIComponent comp, BookDTO book) {
        if (book == null || book.getId() == null)
            return "";
        
        return String.valueOf(book.getId());
    }
}