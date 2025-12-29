package br.elibrary.service;

import java.util.List;

import br.elibrary.dto.LoanDTO;
import br.elibrary.dto.UserDTO;
import jakarta.ejb.Remote;

@Remote
public interface UserSessionService {
	
	boolean login(String registration, String password);

	UserDTO getLoggedInUser();

	void logout();

	boolean borrowCopy(Long copyId);

	boolean returnCopy(Long copyId);

	List<LoanDTO> getActiveLoans();
}
