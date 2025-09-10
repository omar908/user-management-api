package com.omar.user_management_api.repository;

import com.omar.user_management_api.domain.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    void updateExistingUser(UUID id, User user);
    void removeEmailMapping(String email);
    void addEmailMapping(String email, UUID id);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    boolean deleteById(UUID id);
}


