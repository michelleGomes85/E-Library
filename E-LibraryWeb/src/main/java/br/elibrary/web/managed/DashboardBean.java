package br.elibrary.web.managed;

import java.io.Serializable;
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
    private SessionBean sessionBean;

    private List<Object[]> booksWithAvailableCount;
    private List<Loan> activeLoans;

    @PostConstruct
    public void init() {
        refresh();
    }

    private UserSessionService getUserSession() {
        return sessionBean.getUserStateful();
    }

    public void refresh() {
        booksWithAvailableCount = bookService.findBooksWithCopyStats();
        activeLoans = getUserSession().getActiveLoans();
    }

    public void borrow(Book book) {

        Copy availableCopy = bookService.findFirstAvailableCopy(book.getId());
        if (availableCopy == null) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Nenhuma cópia disponível.");
            return;
        }

        boolean success = getUserSession().borrowCopy(availableCopy.getId());

        if (success) {
            addMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Livro emprestado!");
            refresh();
        } else {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao emprestar.");
        }
    }

    public void returnCopy(Loan loan) {
        boolean success = getUserSession().returnCopy(loan.getCopy().getId());

        if (success) {
            addMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Livro devolvido!");
            refresh();
        } else {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao devolver.");
        }
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

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
}