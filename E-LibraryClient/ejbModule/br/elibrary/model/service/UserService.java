package br.elibrary.model.service;

import br.elibrary.model.User;

public interface UserService {
    User create(User user);
    User update(User user);
    void delete(User user);
    User findByRegistration(String registration);
    User findById(Long id);
}
