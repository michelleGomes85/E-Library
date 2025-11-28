package br.elibrary.web.managed;

import java.io.Serializable;

import br.elibrary.model.User;
import br.elibrary.service.UserSessionService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

@Named("sessionBean")
@SessionScoped
public class SessionBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private User loggedUser;
    private UserSessionService userStateful;

    public User getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(User user) {
        this.loggedUser = user;
    }

    public boolean isLogged() {
        return loggedUser != null;
    }

    public UserSessionService getUserStateful() {
        return userStateful;
    }

    public void setUserStateful(UserSessionService userStateful) {
        this.userStateful = userStateful;
    }
}

