package com.omar.user_management_api.service;

import com.omar.user_management_api.domain.User;
import com.omar.user_management_api.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public UserServiceImpl(UserRepository userRepository,
                           ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public User createUser(String name, String email) {
        userRepository.findByEmail(email).ifPresent(userFound -> {
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

    public record UserCreatedEvent(UUID userId) { }
}


