package com.omar.user_management_api.service;

import com.omar.user_management_api.domain.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User createUser(String name, String email);
    Optional<User> getUser(UUID id);
    List<User> listUsers();
    boolean deleteUser(UUID id);
}


