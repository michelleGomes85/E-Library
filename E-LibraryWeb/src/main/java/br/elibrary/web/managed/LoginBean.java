package br.elibrary.web.managed;

import java.io.Serializable;

import br.elibrary.model.User;
import br.elibrary.model.enuns.Rules;
import br.elibrary.model.service.UserSessionService;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("loginBean")
@RequestScoped
public class LoginBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private UserSessionService userSession;

    @Inject
    private SessionBean sessionBean;

    private String registration;
    private String password;

    public String doLogin() {

        boolean ok;

        try {
            ok = userSession.login(registration, password);
        } catch (Exception e) {
            addError("Erro no servidor ao tentar fazer login.");
            return null;
        }

        if (!ok) {
            addError("Matrícula ou senha inválidos.");
            return null;
        }

        User user = userSession.getLoggedInUser();

        sessionBean.setLoggedUser(user);
        sessionBean.setUserStateful(userSession);

        FacesContext.getCurrentInstance()
                .getExternalContext()
                .getSessionMap()
                .put("loggedUser", user);

        if (user.getRules() == Rules.ADMIN) {
            return "/admin/index?faces-redirect=true";
        } else {
            return "/user/index?faces-redirect=true";
        }
    }

    public String doLogout() {

        try {
            if (sessionBean.getUserStateful() != null) {
                sessionBean.getUserStateful().logout();
            }
        } catch (Exception ignore) {}

        sessionBean.setLoggedUser(null);
        sessionBean.setUserStateful(null);

        FacesContext.getCurrentInstance()
                .getExternalContext()
                .invalidateSession();

        return "/login?faces-redirect=true";
    }

    private void addError(String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
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
}