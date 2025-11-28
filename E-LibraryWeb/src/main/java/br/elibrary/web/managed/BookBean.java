package br.elibrary.web.managed;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import org.primefaces.PrimeFaces;

import br.elibrary.model.Book;
import br.elibrary.service.BookService;

@Named
@ViewScoped
public class BookBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private BookService bookService;

    private Book book = new Book();
    
    private Book selectedBook;
    
    private List<Book> books;
    
    private List<Book> filteredBooks;      

    private boolean editMode = false;      

    @PostConstruct
    public void init() {
        loadBooks();
    }

    public void loadBooks() {
        books = bookService.findAll();
    }

    public void openNew() {
        book = new Book();
        editMode = false;
    }

    public void edit(Book book) {
        this.book = book;
        this.editMode = true;
    }

    public void confirmDelete(Book book) {
        this.selectedBook = book;
    }

    public void save() {
        try {
            validateBook(book);
            
            if (editMode) {
                book = bookService.update(book);
                addMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Livro atualizado com sucesso!");
            } else {
                book = bookService.create(book);
                addMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Livro '" + book.getTitle() + "' cadastrado com sucesso!");
            }

            loadBooks();
            
            PrimeFaces.current().executeScript("PF('manageBookDialog').hide();");

        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao salvar livro: " + e.getMessage());
        }
    }

    public void delete() {
    	
        try {
        	
            if (selectedBook == null || selectedBook.getId() == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Nenhum livro selecionado para exclusão.");
                return;
            }

            bookService.delete(selectedBook);
            addMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Livro excluído com sucesso!");
            loadBooks();
            
            PrimeFaces.current().executeScript("PF('deleteBookDialog').hide();");
        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao excluir livro: " + e.getMessage());
        }
    }

    private void validateBook(Book book) throws IllegalArgumentException {
    	
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Título é obrigatório.");
        }
        
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new IllegalArgumentException("Autor é obrigatório.");
        }
        
        if (book.getPublisher() == null || book.getPublisher().trim().isEmpty()) {
            throw new IllegalArgumentException("Editora é obrigatória.");
        }
        
        Integer year = book.getYear();
        int currentYear = java.time.Year.now().getValue();
        if (year == null || year < 1000 || year > currentYear + 2) {
            throw new IllegalArgumentException("Ano inválido.");
        }
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Book getSelectedBook() {
        return selectedBook;
    }

    public void setSelectedBook(Book selectedBook) {
        this.selectedBook = selectedBook;
    }

    public List<Book> getBooks() {
        return books != null ? books : new ArrayList<>();
    }

    public List<Book> getFilteredBooks() {
        return filteredBooks;
    }

    public void setFilteredBooks(List<Book> filteredBooks) {
        this.filteredBooks = filteredBooks;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }
}