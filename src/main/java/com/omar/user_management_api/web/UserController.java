package com.omar.user_management_api.web;

import com.omar.user_management_api.domain.User;
import com.omar.user_management_api.dto.CreateUserRequest;
import com.omar.user_management_api.dto.UpdateUserRequest;
import com.omar.user_management_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collections;
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
    public ResponseEntity<List<User>> list() {
        var listUsers = userService.listUsers();
        return listUsers.isEmpty() ?
                ResponseEntity.status(HttpStatus.OK).body(Collections.emptyList()) :
                ResponseEntity.status(HttpStatus.OK).body(listUsers);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@Valid @RequestBody UpdateUserRequest request, UUID id) {
        User updatedUser = userService.updateUser(id, request.getName(), request.getEmail());
        return ResponseEntity.ok().body(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}


