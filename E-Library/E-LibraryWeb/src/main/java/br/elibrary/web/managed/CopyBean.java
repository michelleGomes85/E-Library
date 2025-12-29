package br.elibrary.web.managed;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.primefaces.PrimeFaces;

import br.elibrary.dto.BookDTO;
import br.elibrary.dto.CopyDTO;
import br.elibrary.model.enuns.CopyStatus;
import br.elibrary.service.BookService;
import br.elibrary.service.CopyService;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named
@ViewScoped
public class CopyBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private CopyService copyService;

    @EJB
    private BookService bookService;

    private CopyDTO copy = new CopyDTO();
    
    private CopyDTO selectedCopy;

    private List<CopyDTO> copies;
    
    private List<CopyDTO> filteredCopies;

    private List<BookDTO> books;

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
        copy = new CopyDTO();
        editMode = false;
    }

    public void edit(CopyDTO copy) {
    	this.copy = copyService.findById(copy.getId());
        this.editMode = true;
    }

    public void confirmDelete(CopyDTO copy) {
        this.selectedCopy = copy;
    }

    public void save() {
        try {
        	
        	if (!validateCopy(copy)) {
    			addMessage(FacesMessage.SEVERITY_WARN, "Formulário inválido", "Corrija os campos destacados.");
    			return;
    		}

            if (editMode) {
                copy = copyService.update(copy);
                addMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Exemplar atualizado com sucesso!");
            } else {
                copy = copyService.create(copy);
                addMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Exemplar '" + copy.getInternalCode() + "' cadastrado com sucesso!");
            }

            loadCopies();

            PrimeFaces.current().executeScript("PF('manageCopyDialog').hide();");

        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao salvar exemplar: " + e.getMessage());
        }
    }

    private boolean validateCopy(CopyDTO copy) throws IllegalArgumentException {

    	boolean valid = true;
        if (copy.getInternalCode() == null || copy.getInternalCode().trim().isEmpty()) {
            addFieldError("internalCode", "Código interno é obrigatório.");
            valid = false;
        }
        
        if (copy.getBookId() == null) {
            addFieldError("book", "Selecione um livro.");
            valid = false;
        }
        
        return valid;
    }
    
	private void addFieldError(String clientId, String message) {
		FacesContext.getCurrentInstance().addMessage("dialogs:" + clientId, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", message));
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
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao excluir exemplar: " + e.getMessage());
        }
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    public CopyDTO getCopy() {
        return copy;
    }

    public void setCopy(CopyDTO copy) {
        this.copy = copy;
    }

    public CopyDTO getSelectedCopy() {
        return selectedCopy;
    }

    public void setSelectedCopy(CopyDTO selectedCopy) {
        this.selectedCopy = selectedCopy;
    }

    public List<CopyDTO> getCopies() {
        return copies != null ? copies : new ArrayList<>();
    }

    public void setCopies(List<CopyDTO> copies) {
        this.copies = copies;
    }

    public List<CopyDTO> getFilteredCopies() {
        return filteredCopies;
    }

    public void setFilteredCopies(List<CopyDTO> filteredCopies) {
        this.filteredCopies = filteredCopies;
    }

    public List<BookDTO> getBooks() {
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
