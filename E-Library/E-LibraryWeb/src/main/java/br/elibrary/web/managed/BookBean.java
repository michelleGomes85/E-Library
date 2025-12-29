package br.elibrary.web.managed;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.primefaces.PrimeFaces;

import br.elibrary.dto.BookDTO;
import br.elibrary.dto.CategoryDTO;
import br.elibrary.service.BookService;
import br.elibrary.service.CategoryService;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named
@ViewScoped
public class BookBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@EJB
	private BookService bookService;

	@EJB
	private CategoryService categoryService;

	private BookDTO book = new BookDTO();
	private BookDTO selectedBook;
	private List<BookDTO> books;
	private List<BookDTO> filteredBooks;
	private List<CategoryDTO> availableCategories;
	private boolean editMode = false;

	@PostConstruct
	public void init() {
		loadBooks();
		loadAvailableCategories();
	}

	public void loadBooks() {
		books = bookService.findAll();
	}

	private void loadAvailableCategories() {
		availableCategories = categoryService.findAll();
	}

	public void openNew() {
		book = new BookDTO();
		editMode = false;
	}

	public void edit(BookDTO book) {
		this.book = bookService.findById(book.getId());
		this.editMode = true;
	}

	public void confirmDelete(BookDTO book) {
		this.selectedBook = book;
	}

	public void save() {

		if (!validateBook(book)) {
			addMessage(FacesMessage.SEVERITY_WARN, "Formulário inválido", "Corrija os campos destacados.");
			return;
		}

		try {
			if (editMode) {
				book = bookService.update(book);
				addMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Livro atualizado com sucesso!");
			} else {
				book = bookService.create(book);
				addMessage(FacesMessage.SEVERITY_INFO, "Sucesso",
						"Livro '" + book.getTitle() + "' cadastrado com sucesso!");
			}

			loadBooks();
			PrimeFaces.current().executeScript("PF('manageBookDialog').hide();");

		} catch (Exception e) {
			e.printStackTrace();
			addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao salvar livro. Verifique os dados e tente novamente.");
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

	private boolean validateBook(BookDTO book) {
		
		boolean valid = true;

		if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
			addFieldError("title", "Título é obrigatório.");
			valid = false;
		}

		if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
			addFieldError("author", "Autor é obrigatório.");
			valid = false;
		}

		if (book.getPublisher() == null || book.getPublisher().trim().isEmpty()) {
			addFieldError("publisher", "Editora é obrigatória.");
			valid = false;
		}

		Integer year = book.getYear();
		int currentYear = java.time.Year.now().getValue();
		if (year == null || year < 1000 || year > currentYear + 2) {
			addFieldError("year", "Ano inválido. Deve estar entre 1000 e " + (currentYear + 2) + ".");
			valid = false;
		}

		if (book.getCategoryIds() == null || book.getCategoryIds().isEmpty()) {
			addFieldError("categories", "Selecione pelo menos uma categoria.");
			valid = false;
		}

		return valid;
	}

	private void addFieldError(String clientId, String message) {
		FacesContext.getCurrentInstance().addMessage("dialogs:" + clientId, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", message));
	}

	private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
	}

	public BookDTO getBook() {
		return book;
	}

	public void setBook(BookDTO book) {
		this.book = book;
	}

	public BookDTO getSelectedBook() {
		return selectedBook;
	}

	public void setSelectedBook(BookDTO selectedBook) {
		this.selectedBook = selectedBook;
	}

	public List<BookDTO> getBooks() {
		return books != null ? books : new ArrayList<>();
	}

	public List<BookDTO> getFilteredBooks() {
		return filteredBooks;
	}

	public void setFilteredBooks(List<BookDTO> filteredBooks) {
		this.filteredBooks = filteredBooks;
	}

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public List<CategoryDTO> getAvailableCategories() {
		return availableCategories != null ? availableCategories : new ArrayList<>();
	}
}