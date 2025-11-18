package br.elibrary.web.managed;

import br.elibrary.model.User;
import br.elibrary.model.enuns.UserType;
import br.elibrary.model.service.UserService;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

@Named
@RequestScoped
public class RegisterBean {

	@EJB
	private UserService userSB;

	private User newUser = new User();
	private String message;

	public RegisterBean() {
		newUser.setType(UserType.STUDENT);
	}

	public void doRegister() {
		try {
			newUser.setPasswordHash(newUser.getPasswordHash());

			userSB.create(newUser);
			message = "Usuário cadastrado com sucesso! Você já pode fazer login.";
		} catch (IllegalArgumentException e) {
			message = e.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
			message = "Erro ao cadastrar usuário. Tente novamente.";
		}
	}

	public User getNewUser() {
		return newUser;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String[] getUserTypes() {
		return new String[] { "STUDENT", "TEACHER", "ADMIN" };
	}
}