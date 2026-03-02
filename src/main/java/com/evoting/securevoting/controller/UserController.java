package com.evoting.securevoting.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.evoting.securevoting.entity.User;
import com.evoting.securevoting.repository.UserRepository;
import com.evoting.securevoting.entity.Role;


import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    

    public UserController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

  @PostMapping("/add")
public ResponseEntity<?> addUser(@RequestBody User user) {

    // Check if email already exists
    if (userRepository.findByEmail(user.getEmail()).isPresent()) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Email already exists");
    }

    user.setPassword(passwordEncoder.encode(user.getPassword()));

    if (user.getRole() == null) {
        user.setRole(Role.VOTER);
    }

    User savedUser = userRepository.save(user);

    return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
}

   @PostMapping("/login")
public ResponseEntity<String> loginUser(@RequestBody User loginRequest) {

    Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());

    if (optionalUser.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    User user = optionalUser.get();

    if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
    }

    return ResponseEntity.ok("Login successful");
}
    }

