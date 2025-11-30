package br.elibrary.service;

import java.util.List;

import br.elibrary.dto.UserDTO;

public interface UserService {
	
	UserDTO create(UserDTO user);
    
	UserDTO update(UserDTO user);
    
    void delete(UserDTO user);
    
    void deleteById(Long id);
    
    List<UserDTO> findAll();
    
    UserDTO findByRegistration(String registration);
    UserDTO findById(Long id);
}
