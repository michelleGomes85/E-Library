package br.elibrary.stateful;

import br.elibrary.model.Copy;
import br.elibrary.model.User;
import br.elibrary.model.service.UserSessionService;
import jakarta.ejb.Remove;
import jakarta.ejb.Stateful;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateful
public class UserSessionSB implements UserSessionService {

    @PersistenceContext(unitName = "E-Library")
    private EntityManager em;

    private User currentUser;

    @Override
    public boolean login(String registration, String passwordHash) {
    	
        User user = em.createQuery(
            "SELECT u FROM User u WHERE u.registration = :reg AND u.passwordHash = :pwd", User.class)
            .setParameter("reg", registration)
            .setParameter("pwd", passwordHash)
            .getResultStream()
            .findFirst()
            .orElse(null);

        if (user != null) {
            this.currentUser = user;
            return true;
        }
        
        return false;
    }

    @Override
    public User getLoggedInUser() {
        return currentUser;
    }

    @Override
    public boolean borrowCopy(Copy copy) {
        return true;
    }

    @Override
    public boolean returnCopy(Copy copy) {
        return true;
    }

    @Override
    @Remove
    public void logout() {
        this.currentUser = null;
    }
}
