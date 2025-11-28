package br.elibrary.web.managed;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.primefaces.PrimeFaces;

import br.elibrary.model.User;
import br.elibrary.model.enuns.Rules;
import br.elibrary.model.enuns.UserType;
import br.elibrary.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named
@ViewScoped
public class UserBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@EJB
	private UserService userService;

	private User user = new User();
	private User selectedUser;

	private List<User> users;
	private List<User> filteredUsers;

	private String password;
	private String passwordConfirm;

	private boolean editMode = false;

	@PostConstruct
	public void init() {
		loadUsers();
	}

	public void loadUsers() {
		users = userService.findAll();
	}

	public void openNew() {
		user = new User();
		editMode = false;
	}

	public void edit(User u) {
		this.user = u;
		this.editMode = true;
	}

	public void confirmDelete(User u) {
		this.selectedUser = u;
	}

	public void save() {
	    try {
	        validate(user);

	        if (!editMode) {
	        	
	            if (password == null || password.trim().isEmpty()) {
	                throw new IllegalArgumentException("A senha é obrigatória para novo usuário.");
	            }
	            
	            if (!password.equals(passwordConfirm)) {
	                throw new IllegalArgumentException("As senhas não coincidem.");
	            }
	            
	            if (password.length() < 6) {
	                throw new IllegalArgumentException("A senha deve ter pelo menos 6 caracteres.");
	            }
	            
	            user.setPasswordHash(password); 
	        } else {
	        	
	            if (password != null && !password.trim().isEmpty()) {
	                if (!password.equals(passwordConfirm)) {
	                    throw new IllegalArgumentException("As senhas não coincidem.");
	                }
	                
	                if (password.length() < 6) {
	                    throw new IllegalArgumentException("A senha deve ter pelo menos 6 caracteres.");
	                }
	                user.setPasswordHash(password);
	            }
	        }

	        if (editMode) {
	            userService.update(user);
	            addMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Usuário atualizado com sucesso!");
	        } else {
	            userService.create(user);
	            addMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Usuário cadastrado com sucesso!");
	        }

	        loadUsers();

	        password = "";
	        passwordConfirm = "";

	        PrimeFaces.current().executeScript("PF('manageUserDialog').hide();");

	    } catch (IllegalArgumentException e) {
	        addMessage(FacesMessage.SEVERITY_WARN, "Atenção", e.getMessage());
	    } catch (Exception e) {
	        e.printStackTrace();
	        addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Não foi possível salvar o usuário. Verifique os dados e tente novamente.");
	    }
	}

	public void delete() {
		try {
			if (selectedUser == null || selectedUser.getId() == null) {
				addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Nenhum usuário selecionado.");
				return;
			}

			userService.delete(selectedUser);
			addMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Usuário excluído!");

			loadUsers();

			PrimeFaces.current().executeScript("PF('deleteUserDialog').hide();");

		} catch (Exception e) {
			addMessage(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
		}
	}

	private void validate(User u) {
		if (u.getName() == null || u.getName().isBlank())
			throw new IllegalArgumentException("Nome é obrigatório.");

		if (u.getEmail() == null || u.getEmail().isBlank())
			throw new IllegalArgumentException("Email é obrigatório.");

		if (u.getRegistration() == null || u.getRegistration().isBlank())
			throw new IllegalArgumentException("Matrícula é obrigatória.");

		if (u.getType() == null)
			throw new IllegalArgumentException("Tipo é obrigatório.");

		if (u.getRules() == null)
			throw new IllegalArgumentException("Regra é obrigatória.");
	}

	private void addMessage(FacesMessage.Severity s, String summary, String detail) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(s, summary, detail));
	}

	public List<UserType> getUserTypes() {
		return Arrays.asList(UserType.values());
	}

	public List<Rules> getRulesList() {
		return Arrays.asList(Rules.values());
	}

	// getters e setters

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getSelectedUser() {
		return selectedUser;
	}

	public void setSelectedUser(User selectedUser) {
		this.selectedUser = selectedUser;
	}

	public List<User> getUsers() {
		return users != null ? users : new ArrayList<>();
	}

	public List<User> getFilteredUsers() {
		return filteredUsers;
	}

	public void setFilteredUsers(List<User> filteredUsers) {
		this.filteredUsers = filteredUsers;
	}

	public boolean isEditMode() {
		return editMode;
	}

	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
