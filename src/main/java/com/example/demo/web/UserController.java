package com.example.demo.web;

import com.example.demo.domain.User;
import com.example.demo.dto.CreateUserRequest;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody CreateUserRequest request) {
        User created = userService.createUser(request.getName(), request.getEmail());
        return ResponseEntity.created(URI.create("/api/users/" + created.getId())).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable UUID id) {
        Optional<User> user = userService.getUser(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping
    public List<User> list() {
        return userService.listUsers();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        boolean deleted = userService.deleteUser(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}


