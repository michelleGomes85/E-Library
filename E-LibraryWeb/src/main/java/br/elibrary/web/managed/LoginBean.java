package br.elibrary.web.managed;

import java.io.Serializable;

import org.primefaces.PrimeFaces;

import br.elibrary.model.User;
import br.elibrary.model.enuns.Rules;
import br.elibrary.service.UserSessionService;
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
    private UserSessionBean sessionBean;

    private String registration;
    private String password;

    public void doLogin() {
        try {
            boolean ok = userSession.login(registration, password);

            if (!ok) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Erro de autenticação", "Matrícula ou senha inválidos.");
                return;
            }

            User user = userSession.getLoggedInUser();
            sessionBean.setLoggedUser(user);
            sessionBean.setUserStateful(userSession);

            FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .getSessionMap()
                    .put("loggedUser", user);

            String target = (user.getRules() == Rules.ADMIN) ? "admin/index.xhtml"
                    : "user/index.xhtml";

            FacesContext.getCurrentInstance().getExternalContext().redirect(target);

        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro interno", 
                    "Não foi possível processar o login. Tente novamente mais tarde.");
        }
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(severity, summary, detail));
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