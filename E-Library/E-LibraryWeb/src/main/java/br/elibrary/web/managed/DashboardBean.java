package br.elibrary.web.managed;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.primefaces.PrimeFaces;

import br.elibrary.dto.BookDTO;
import br.elibrary.dto.CopyDTO;
import br.elibrary.dto.LoanDTO;
import br.elibrary.exception.BusinessException;
import br.elibrary.model.enuns.CopyStatus;
import br.elibrary.service.BookService;
import br.elibrary.service.CatalogStatusService;
import br.elibrary.service.CopyService;
import br.elibrary.service.UserSessionService;
import br.elibrary.service.WaitingListService;
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

	@EJB
	private CopyService copyService;

	@Inject
	private UserSessionBean sessionBean;
	
	@EJB
	private WaitingListService waitingListService;

	private List<Object[]> booksWithAvailableCount;

	private List<LoanDTO> activeLoans;

	private BookDTO selectedBook;
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

		if (showOnlyUnavailable)
			booksWithAvailableCount = bookService.findUnavailableBooksWithStats();
		else if (searchQuery != null && !searchQuery.trim().isEmpty())
			booksWithAvailableCount = bookService.findByTitleOrAuthorWithStats(searchQuery.trim());
		else
			booksWithAvailableCount = bookService.findBooksWithCopyStats();

		if (booksWithAvailableCount == null) {
			booksWithAvailableCount = Collections.emptyList();
		}
	}

	public void borrow(BookDTO book) {
		
		try {
			CopyDTO availableCopy = bookService.findFirstAvailableCopy(book.getId());

			if (availableCopy == null) {
				addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Nenhuma cópia disponível.");
				return;
			}

			copyService.updateStatus(availableCopy.getId(), CopyStatus.RESERVED);

			boolean success = getUserSession().borrowCopy(availableCopy.getId());

			if (success) {
				refresh();
				addMessage(FacesMessage.SEVERITY_INFO, "Sucesso",
						String.format("Aproveite o livro: %s, e explore outros títulos!", book.getTitle()));
				PrimeFaces.current().executeScript("PF('borrowSuccessDialog').show();");
			} else {
				addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao emprestar.");
			}
		} catch (BusinessException e) {
			refresh();
			addMessage(FacesMessage.SEVERITY_ERROR, "Inconsistência Detectada", 
		            "O status deste livro foi alterado recentemente. A lista foi atualizada.");
		} catch (Exception e) {
	        addMessage(FacesMessage.SEVERITY_ERROR, "Erro Crítico", "Erro ao processar: " + e.getMessage());
	    }
	}

	public void returnCopy(LoanDTO loan) {

		if (loan == null || loan.getCopyId() == null) {
			addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Cópia inválida.");
			return;
		}

		boolean success = getUserSession().returnCopy(loan.getCopyId());

		if (success) {
			refresh();
			addMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Livro devolvido!");
		} else
			addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao devolver.");
	}

	public void selectBook(BookDTO book, int total, int available) {
		this.selectedBook = bookService.findById(book.getId());
		this.detailTotal = total;
		this.detailAvailable = available;
	}

	public List<String> getBookCategories() {

		if (selectedBook == null || selectedBook.getCategoryIds() == null) {
			return List.of();
		}

		return selectedBook.getCategoryNameList();
	}

	public List<Object[]> getBooksWithAvailableCount() {
		return booksWithAvailableCount;
	}

	public List<LoanDTO> getActiveLoans() {
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

	public BookDTO getSelectedBook() {
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

	public void onSearchChange() {
	}

	public void onFilterChange() {
	}

	public void clearSearch() {
		searchQuery = "";
		showOnlyUnavailable = false;
		refresh();
	}

	private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
	}
	
	public void subscribeWaitingList(BookDTO book) {

	    Long userId = sessionBean.getLoggedUser().getId();
	    Long bookId = book.getId();

	    waitingListService.subscribe(userId, bookId);

	    FacesContext.getCurrentInstance().addMessage(
	        null,
	        new FacesMessage(
	            FacesMessage.SEVERITY_INFO,
	            "Lista de espera",
	            "Você foi adicionado à lista de espera do livro."
	        )
	    );
	}
}