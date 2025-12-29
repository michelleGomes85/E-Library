package br.elibrary.web.managed;

import java.io.Serializable;

import br.elibrary.dto.UserDTO;
import br.elibrary.service.UserSessionService;
import br.elibrary.service.UserSessionServiceLocal;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

@Named("sessionBean")
@SessionScoped
public class UserSessionBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private UserDTO loggedUser;
    
    private UserSessionServiceLocal userStateful;

    public UserDTO getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(UserDTO user) {
        this.loggedUser = user;
    }

    public boolean isLogged() {
        return loggedUser != null;
    }

    public UserSessionService getUserStateful() {
        return userStateful;
    }

    public void setUserStateful(UserSessionServiceLocal userStateful) {
        this.userStateful = userStateful;
    }
}

