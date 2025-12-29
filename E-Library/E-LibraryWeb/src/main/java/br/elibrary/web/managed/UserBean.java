package br.elibrary.web.managed;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.primefaces.PrimeFaces;

import br.elibrary.dto.UserDTO;
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

	private UserDTO user = new UserDTO();
	private UserDTO selectedUser;

	private List<UserDTO> users;
	private List<UserDTO> filteredUsers;

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
        user = new UserDTO();
        password = "";
        passwordConfirm = "";
        editMode = false;
    }

    public void edit(UserDTO u) {
    	
        this.user = userService.findById(u.getId());
        password = "";
        passwordConfirm = "";
        editMode = true;
    }

	public void confirmDelete(UserDTO u) {
		this.selectedUser = u;
	}

	public void save() {
        try {
            user.setPassword(password);

            if (editMode) {
                user = userService.update(user);
                addMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Usuário atualizado com sucesso!");
            } else {
                user = userService.create(user);
                addMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Usuário cadastrado com sucesso!");
            }

            loadUsers();
            PrimeFaces.current().executeScript("PF('manageUserDialog').hide();");

        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        } finally {
            password = "";
            passwordConfirm = "";
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

	private void addMessage(FacesMessage.Severity s, String summary, String detail) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(s, summary, detail));
	}

	public List<UserType> getUserTypes() {
		return Arrays.asList(UserType.values());
	}

	public List<Rules> getRulesList() {
		return Arrays.asList(Rules.values());
	}

	public UserDTO getUser() {
		return user;
	}

	public void setUser(UserDTO user) {
		this.user = user;
	}

	public UserDTO getSelectedUser() {
		return selectedUser;
	}

	public void setSelectedUser(UserDTO selectedUser) {
		this.selectedUser = selectedUser;
	}

	public List<UserDTO> getUsers() {
		return users != null ? users : new ArrayList<>();
	}

	public List<UserDTO> getFilteredUsers() {
		return filteredUsers;
	}

	public void setFilteredUsers(List<UserDTO> filteredUsers) {
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
	
	public void registerPublic() {
	    try {
	        user.setRules(Rules.COMMON_USER);
	        user.setPassword(password);

	        userService.create(user);

	        addMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Cadastro realizado com sucesso! Agora você pode fazer login.");
	        
	        user = new UserDTO();
	        password = "";
	        passwordConfirm = "";

	    } catch (IllegalArgumentException e) {
	    	System.out.println("Aqui - 1");
	        addMessage(FacesMessage.SEVERITY_WARN, "Atenção", e.getMessage());
	    } catch (Exception e) {
	    	System.out.println("Aqui - 2");
	        addMessage(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
	    }
	}
}
