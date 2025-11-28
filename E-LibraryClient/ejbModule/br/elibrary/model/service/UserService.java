package br.elibrary.model.service;

import java.util.List;

import br.elibrary.model.User;

public interface UserService {
    User create(User user);
    User update(User user);
    void delete(User user);
    List<User> findAll();
    User findByRegistration(String registration);
    User findById(Long id);
}
