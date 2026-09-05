package com.relay.server.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.relay.server.user.domain.User;
import com.relay.server.user.repository.UserRepository;

/**
 * Responsible for executing user related operations.
 */
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public void createUser(String username) {
        User newUser = new User(username);
        repository.save(newUser);
    }

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public Optional<User> findUser(String username) {
        return repository.findByName(username);
    }

    public String getUsernameByID(UUID id) {
        return repository.findByID(id).map(User::getUsername).orElse(null);
    }
}
