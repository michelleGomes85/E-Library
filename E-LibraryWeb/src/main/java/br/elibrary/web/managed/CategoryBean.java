package br.elibrary.web.managed;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.primefaces.PrimeFaces;

import br.elibrary.model.Category;
import br.elibrary.service.CategoryService;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named
@ViewScoped
public class CategoryBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@EJB
	private CategoryService categoryService;

	private Category category = new Category();

	private Category selectedCategory;

	private List<Category> categories;

	private boolean editMode = false;

	@PostConstruct
	public void init() {
		loadCategories();
	}

	public void loadCategories() {
		categories = categoryService.findAll();
	}

	public void openNew() {
		category = new Category();
		editMode = false;
	}

	public void edit(Category cat) {
		this.category = cat;
		this.editMode = true;
	}

	public void confirmDelete(Category cat) {
		this.selectedCategory = cat;
	}

	public void save() {

		if (!validateCategory()) {
			addMessage(FacesMessage.SEVERITY_WARN, "Formulário inválido", "Corrija os campos destacados.");
			return;
		}

		try {
			if (editMode) {
				category = categoryService.update(category);
				addMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Categoria atualizada com sucesso!");
			} else {
				category = categoryService.create(category);
				addMessage(FacesMessage.SEVERITY_INFO, "Sucesso",
						"Categoria '" + category.getName() + "' criada com sucesso!");
			}

			loadCategories();
			PrimeFaces.current().executeScript("PF('manageCategoryDialog').hide();");

		} catch (Exception e) {
			e.printStackTrace();
			addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao salvar categoria. Verifique os dados.");
		}
	}

	public void delete() {
		try {
			if (selectedCategory == null || selectedCategory.getId() == null) {
				addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Nenhuma categoria selecionada.");
				return;
			}

			categoryService.delete(selectedCategory);
			addMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Categoria excluída com sucesso!");
			loadCategories();
			PrimeFaces.current().executeScript("PF('deleteCategoryDialog').hide();");

		} catch (Exception e) {
			e.printStackTrace();
			addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao excluir categoria. Ela pode estar em uso.");
		}
	}

	private boolean validateCategory() {
		boolean valid = true;
		if (category.getName() == null || category.getName().trim().isEmpty()) {
			addFieldError("name", "Nome da categoria é obrigatório.");
			valid = false;
		}
		return valid;
	}

	private void addFieldError(String clientId, String message) {
		FacesContext.getCurrentInstance().addMessage("dialogs:" + clientId,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, "", message));
	}

	private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Category getSelectedCategory() {
		return selectedCategory;
	}

	public void setSelectedCategory(Category selectedCategory) {
		this.selectedCategory = selectedCategory;
	}

	public List<Category> getCategories() {
		return categories != null ? categories : new ArrayList<>();
	}

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}
}
