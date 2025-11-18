package br.elibrary.model.service;

import br.elibrary.model.Copy;
import br.elibrary.model.User;

public interface UserSessionService {
    boolean login(String registration, String passwordHash);
    User getLoggedInUser();
    boolean borrowCopy(Copy copy);
    boolean returnCopy(Copy copy);
    void logout();
}
