package com.evoting.securevoting.controller;

import com.evoting.securevoting.entity.User;
import com.evoting.securevoting.repository.UserRepository;
import com.evoting.securevoting.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.evoting.securevoting.entity.Role;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // -------- LOGIN --------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {

        Optional<User> userOpt = userRepository.findByEmail(request.get("email").trim());

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid email or password");
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(request.get("password"), user.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid email or password");
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return ResponseEntity.ok(
                Map.of(
                        "token", token,
                        "role", user.getRole().name()
                )
        );
    }

    // -------- REGISTER --------
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {

        String fullName = request.get("fullName");
        String email = request.get("email");
        String password = request.get("password");
        String role = request.get("role");

        // Check if email already exists
        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Email already registered"));
        }

        // Create new user
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // encrypt password
        if (role == null || role.isEmpty() || !role.equalsIgnoreCase("ADMIN")) {
    user.setRole(Role.VOTER); // default to VOTER
} else {
    user.setRole(Role.ADMIN);
}
        user.setHasVoted(false);

        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }
}