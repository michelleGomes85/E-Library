package br.elibrary.web.converter;

import br.elibrary.model.Book;
import br.elibrary.model.service.BookService;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.inject.Named;

@Named("bookConverter") 
@ApplicationScoped     
public class BookConverter implements Converter<Book> {

    @EJB
    private BookService bookService;

    @Override
    public Book getAsObject(FacesContext ctx, UIComponent comp, String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        try {
            Long longId = Long.parseLong(id.trim());
            Book book = bookService.findById(longId);
            return book;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext ctx, UIComponent comp, Book book) {
        if (book == null || book.getId() == null) {
            return "";
        }
        return String.valueOf(book.getId());
    }
}