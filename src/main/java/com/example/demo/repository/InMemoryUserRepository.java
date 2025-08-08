package com.example.demo.repository;

import com.example.demo.domain.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<UUID, User> usersById = new ConcurrentHashMap<>();
    private final Map<String, UUID> idByEmail = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        usersById.put(user.getId(), user);
        idByEmail.put(user.getEmail().toLowerCase(), user.getId());
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(usersById.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        UUID id = idByEmail.get(email.toLowerCase());
        if (id == null) return Optional.empty();
        return Optional.ofNullable(usersById.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(usersById.values());
    }

    @Override
    public boolean deleteById(UUID id) {
        User removed = usersById.remove(id);
        if (removed != null) {
            idByEmail.remove(removed.getEmail().toLowerCase());
            return true;
        }
        return false;
    }

    @Override
    public long count() {
        return usersById.size();
    }
}


