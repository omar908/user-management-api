package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.EmailValidator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmailValidator emailValidator;
    private final ApplicationEventPublisher eventPublisher;

    public UserServiceImpl(UserRepository userRepository,
                           EmailValidator emailValidator,
                           ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.emailValidator = emailValidator;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public User createUser(String name, String email) {
        if (!emailValidator.isValid(email)) {
            throw new IllegalArgumentException("Invalid email");
        }
        userRepository.findByEmail(email).ifPresent(u -> {
            throw new IllegalStateException("Email already exists");
        });
        User saved = userRepository.save(User.newUser(name, email));
        eventPublisher.publishEvent(new UserCreatedEvent(saved.getId()));
        return saved;
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

    public static class UserCreatedEvent {
        private final UUID userId;

        public UserCreatedEvent(UUID userId) {
            this.userId = userId;
        }

        public UUID getUserId() {
            return userId;
        }
    }
}


