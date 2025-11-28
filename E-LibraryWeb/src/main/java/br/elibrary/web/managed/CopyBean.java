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
import br.elibrary.model.Copy;
import br.elibrary.model.enuns.CopyStatus;
import br.elibrary.service.BookService;
import br.elibrary.service.CopyService;

@Named
@ViewScoped
public class CopyBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private CopyService copyService;

    @EJB
    private BookService bookService;

    private Copy copy = new Copy();
    
    private Copy selectedCopy;

    private List<Copy> copies;
    private List<Copy> filteredCopies;

    private List<Book> books;

    private boolean editMode = false;

    @PostConstruct
    public void init() {
        loadBooks();
        loadCopies();
    }

    public void loadBooks() {
        books = bookService.findAll();
    }

    public void loadCopies() {
        copies = copyService.findAll();
    }

    public void openNew() {
        copy = new Copy();
        editMode = false;
    }

    public void edit(Copy copy) {
        this.copy = copy;
        this.editMode = true;
    }

    public void confirmDelete(Copy copy) {
        this.selectedCopy = copy;
    }

    public void save() {
        try {
            validateCopy(copy);

            if (editMode) {
                copy = copyService.update(copy);
                addMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Exemplar atualizado com sucesso!");
            } else {
                copy = copyService.create(copy);
                addMessage(FacesMessage.SEVERITY_INFO, "Sucesso", 
                    "Exemplar '" + copy.getInternalCode() + "' cadastrado com sucesso!");
            }

            loadCopies();

            PrimeFaces.current().executeScript("PF('manageCopyDialog').hide();");

        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao salvar exemplar: " + e.getMessage());
        }
    }

    private void validateCopy(Copy copy) throws IllegalArgumentException {

        if (copy.getInternalCode() == null || copy.getInternalCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Código interno é obrigatório.");
        }

        if (copy.getBook() == null || copy.getBook().getId() == null) {
            throw new IllegalArgumentException("Selecione um livro.");
        }
    }

    public void delete() {
        try {
            if (selectedCopy == null || selectedCopy.getId() == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Nenhum exemplar selecionado para exclusão.");
                return;
            }

            copyService.deleteById(selectedCopy.getId());

            addMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Exemplar excluído com sucesso!");
            loadCopies();

            PrimeFaces.current().executeScript("PF('deleteCopyDialog').hide();");

        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao excluir exemplar: " + e.getMessage());
        }
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(severity, summary, detail));
    }

    public Copy getCopy() {
        return copy;
    }

    public void setCopy(Copy copy) {
        this.copy = copy;
    }

    public Copy getSelectedCopy() {
        return selectedCopy;
    }

    public void setSelectedCopy(Copy selectedCopy) {
        this.selectedCopy = selectedCopy;
    }

    public List<Copy> getCopies() {
        return copies != null ? copies : new ArrayList<>();
    }

    public void setCopies(List<Copy> copies) {
        this.copies = copies;
    }

    public List<Copy> getFilteredCopies() {
        return filteredCopies;
    }

    public void setFilteredCopies(List<Copy> filteredCopies) {
        this.filteredCopies = filteredCopies;
    }

    public List<Book> getBooks() {
        return books != null ? books : new ArrayList<>();
    }

    public CopyStatus[] getStatusValues() {
        return CopyStatus.values();
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }
}
