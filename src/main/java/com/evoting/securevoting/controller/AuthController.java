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
import java.time.LocalDate;
import java.time.Period;

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
                user.getRole().name());

        // ✅ FIX: Added fullName and city to login response
        // Frontend needs these to display voter name and filter elections by city
        return ResponseEntity.ok(
                Map.of(
                        "token",    token,
                        "role",     user.getRole().name(),
                        "fullName", user.getFullName(),
                        "city",     user.getCity() != null ? user.getCity() : ""
                ));
    }

    // -------- REGISTER --------
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {

        String fullName     = request.get("fullName");
        String email        = request.get("email");
        String password     = request.get("password");
        String role         = request.get("role");
        String aadhaarNumber = request.get("aadhaarNumber");
        String gender       = request.get("gender");

        if (!aadhaarNumber.matches("\\d{12}")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Aadhaar must be exactly 12 digits"));
        }
        if (userRepository.findByAadhaarNumber(aadhaarNumber).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Aadhaar already registered"));
        }

        String dobStr = request.get("dob");
        LocalDate dob = LocalDate.parse(dobStr);
        String city = request.get("city");

        int age = Period.between(dob, LocalDate.now()).getYears();

        if (age < 18) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "You must be 18 or older to register for voting"));
        }

        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Email already registered"));
        }

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setDob(dob);
        user.setCity(city);
        user.setAadhaarNumber(aadhaarNumber);
        user.setGender(gender);

        if (role == null || role.isEmpty() || !role.equalsIgnoreCase("ADMIN")) {
            user.setRole(Role.VOTER);
        } else {
            user.setRole(Role.ADMIN);
        }
        user.setHasVoted(false);

        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }
}