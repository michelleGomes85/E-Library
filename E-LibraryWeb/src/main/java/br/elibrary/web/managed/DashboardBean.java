package br.elibrary.web.managed;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import br.elibrary.model.Book;
import br.elibrary.model.Copy;
import br.elibrary.model.Loan;
import br.elibrary.service.BookService;
import br.elibrary.service.CatalogStatusService;
import br.elibrary.service.UserSessionService;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@ViewScoped
public class DashboardBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private BookService bookService;

    @EJB
    private CatalogStatusService catalogStatusSB;

    @Inject
    private UserSessionBean sessionBean;

    private List<Object[]> booksWithAvailableCount;
    
    private List<Loan> activeLoans;

    private Book selectedBook;
    private int detailTotal;
    private int detailAvailable;

    private String searchQuery;
    
    private boolean showOnlyUnavailable;

    @PostConstruct
    public void init() {
        refresh();
    }

    private UserSessionService getUserSession() {
        return sessionBean.getUserStateful();
    }

    public void refresh() {
    	
        activeLoans = getUserSession().getActiveLoans();

        if (showOnlyUnavailable) {
            booksWithAvailableCount = bookService.findUnavailableBooksWithStats();
        } else if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            booksWithAvailableCount = bookService.findByTitleOrAuthorWithStats(searchQuery.trim());
        } else {
            booksWithAvailableCount = bookService.findBooksWithCopyStats();
        }

        if (booksWithAvailableCount == null) {
            booksWithAvailableCount = Collections.emptyList();
        }
    }

    public void borrow(Book book) {
        Copy availableCopy = bookService.findFirstAvailableCopy(book.getId());
        if (availableCopy == null) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Nenhuma cópia disponível.");
            return;
        }

        boolean success = getUserSession().borrowCopy(availableCopy.getId());

        if (success) {
            refresh();
            addMessage(FacesMessage.SEVERITY_INFO, "Sucesso",
                String.format("Aproveite o livro: %s, e explore outros títulos!", book.getTitle()));
        } else {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao emprestar.");
        }
    }

    public void returnCopy(Loan loan) {
        boolean success = getUserSession().returnCopy(loan.getCopy().getId());

        if (success) {
            refresh();
            addMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Livro devolvido!");
        } else {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao devolver.");
        }
    }

    public void selectBook(Book book, int total, int available) {
        this.selectedBook = bookService.findById(book.getId());
        this.detailTotal = total;
        this.detailAvailable = available;
    }

    public List<String> getBookCategories() {
        if (selectedBook == null || selectedBook.getCategories() == null) {
            return List.of();
        }
        return selectedBook.getCategories()
                           .stream()
                           .map(c -> c.getName())
                           .sorted()
                           .toList();
    }

    public List<Object[]> getBooksWithAvailableCount() {
        return booksWithAvailableCount;
    }

    public List<Loan> getActiveLoans() {
        return activeLoans;
    }

    public int getTotalBooks() {
        return catalogStatusSB.getTotalBooks();
    }

    public int getTotalCopies() {
        return catalogStatusSB.getTotalCopies();
    }

    public int getAvailableCopies() {
        return catalogStatusSB.getAvailableCopies();
    }

    public Book getSelectedBook() {
        return selectedBook;
    }

    public int getDetailTotal() {
        return detailTotal;
    }

    public int getDetailAvailable() {
        return detailAvailable;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public boolean isShowOnlyUnavailable() {
        return showOnlyUnavailable;
    }

    public void setShowOnlyUnavailable(boolean showOnlyUnavailable) {
        this.showOnlyUnavailable = showOnlyUnavailable;
    }

    public void onSearchChange() {}

    public void onFilterChange() {}

    public void clearSearch() {
        searchQuery = "";
        showOnlyUnavailable = false;
        refresh();
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
}