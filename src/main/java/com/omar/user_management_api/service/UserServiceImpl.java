package com.omar.user_management_api.service;

import com.omar.user_management_api.domain.User;
import com.omar.user_management_api.repository.UserRepository;
import com.omar.user_management_api.util.UserInputNormalizer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(String name, String email) {
        var normalizedName = UserInputNormalizer.normalizeName.apply(name);
        var normalizedEmail = UserInputNormalizer.normalizeEmail.apply(email);
        userRepository.findByEmail(normalizedEmail).ifPresent(userFound -> {
            throw new IllegalStateException("Email already exists");
        });
        return userRepository.save(User.newUser(normalizedName, normalizedEmail));
    }

    @Override
    public User updateUser(UUID id, String name, String email){
        var normalizedName = UserInputNormalizer.normalizeName.apply(name);
        var normalizedEmail = UserInputNormalizer.normalizeEmail.apply(email);
        User updatedUser;

        var storedUser = userRepository.findById(id);
        if (storedUser.isPresent()){
            updatedUser = new User(id, normalizedName, normalizedEmail, storedUser.get().getCreatedAt());
            userRepository.updateExistingUser(id, updatedUser);
            if (!storedUser.get().getEmail().equals(updatedUser.getEmail())) {
                userRepository.removeEmailMapping(storedUser.get().getEmail());
                userRepository.addEmailMapping(updatedUser.getEmail(), updatedUser.getId());
            }
        } else {
            throw new IllegalStateException("User does not exist.");
        }
        return updatedUser;
    }

    @Override
    public Optional<User> getUser(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> listUsers() {
        return userRepository.findAll();
    }

    @Override
    public boolean deleteUser(UUID id) {
        return userRepository.deleteById(id);
    }
}


