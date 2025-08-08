package com.example.demo.service;

import com.example.demo.domain.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User createUser(String name, String email);
    Optional<User> getUser(UUID id);
    List<User> listUsers();
    boolean deleteUser(UUID id);
}


