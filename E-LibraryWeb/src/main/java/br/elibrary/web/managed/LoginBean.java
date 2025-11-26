package br.elibrary.web.managed;

import java.io.Serializable;

import br.elibrary.model.User;
import br.elibrary.model.service.UserSessionService;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

/**
 * Managed Bean responsável pelo processo de login e logout do usuário.
 *
 * <p>
 * Mantém o estado do usuário autenticado utilizando escopo de sessão,
 * permitindo que o sistema saiba quem está logado durante toda a navegação.
 * </p>
 */
@Named("loginBean")
@RequestScoped
public class LoginBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private UserSessionService userSession;

    /** Matrícula usada para autenticação. */
    private String registration;

    /** Senha digitada no formulário. */
    private String password;

    /** Mensagem de erro ou sucesso exibida na interface. */
    private String message;

    /** Usuário logado na sessão atual. */
    private User loggedInUser;

    /**
     * Tenta autenticar o usuário no sistema.
     *
     * <p>
     * Caso o login seja bem-sucedido, o usuário é carregado e mantido na sessão.
     * Caso contrário, apenas exibe uma mensagem de erro.
     * </p>
     *
     * @return navegação da página (com redirect) ou null em caso de falha
     */
    public String doLogin() {

        if (userSession.login(registration, password)) {
            loggedInUser = userSession.getLoggedInUser();
            message = "Login realizado com sucesso!";
            return "index?faces-redirect=true";
        } else {
            message = "Matrícula ou senha inválidos.";
            return null;
        }
    }

    /**
     * Realiza logout do sistema, limpando dados da sessão.
     *
     * @return página de login com redirect
     */
    public String doLogout() {
        userSession.logout();
        loggedInUser = null;
        message = "Você saiu do sistema.";

        return "login?faces-redirect=true";
    }

    // ==============================
    // Getters e Setters
    // ==============================

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

    /**
     * Indica se há um usuário autenticado na sessão.
     *
     * @return true se o usuário está logado
     */
    public boolean isLoggedIn() {
        return loggedInUser != null;
    }
}
