package br.elibrary.model.service;

import java.util.List;

import br.elibrary.model.Loan;
import br.elibrary.model.User;

public interface UserSessionService {
    boolean login(String registration, String password);
    User getLoggedInUser();
    void logout();
    boolean borrowCopy(Long copyId);
    boolean returnCopy(Long copyId);
    List<Loan> getActiveLoans();
}
