package br.elibrary.web.managed;

import java.io.Serializable;

import br.elibrary.model.User;
import br.elibrary.model.service.UserSessionService;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

@Named
@SessionScoped
public class LoginBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@EJB
	private UserSessionService userSession;

	private String registration;
	private String password;
	private String message;
	private User loggedInUser;

	public String doLogin() {
		String passwordHash = password;

		if (userSession.login(registration, passwordHash)) {
			loggedInUser = userSession.getLoggedInUser();
			message = "Login realizado com sucesso!";
			return "index?faces-redirect=true"; 
		} else {
			message = "Matrícula ou senha inválidos.";
			return null;
		}
	}

	public String doLogout() {
		userSession.logout();
		loggedInUser = null;
		message = "Você saiu do sistema.";
		
		return "login?faces-redirect=true";
	}

	public String getRegistration() {
		return registration;
	}

	public void setRegistration(String registration) {
		this.registration = registration;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public User getLoggedInUser() {
		return loggedInUser;
	}

	public boolean isLoggedIn() {
		return loggedInUser != null;
	}
}
