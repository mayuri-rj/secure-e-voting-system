package com.evoting.securevoting.controller;

import com.evoting.securevoting.service.VoteService;
import com.evoting.securevoting.repository.UserRepository;
import com.evoting.securevoting.entity.User;
import com.evoting.securevoting.dto.VoteRequest;
import com.evoting.securevoting.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/vote")
@CrossOrigin
public class VoteController {

    @Autowired
    private VoteService voteService;

    @Autowired
    private UserRepository userRepository;

@PostMapping("/cast")
public ResponseEntity<?> castVote(@RequestBody VoteRequest request) {

    String email = SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getName();

    return voteService.castVote(email, request.getElectionId(), request.getCandidateId());
}

    @GetMapping("/check-voter")
    public ResponseEntity<?> checkVoter(@RequestParam String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body("User not found");

        User user = userOpt.get();

        // ✅ Return essential info only
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("fullName", user.getFullName());
        response.put("email", user.getEmail());
        response.put("role", user.getRole().name()); // Enum → string

        System.out.println("check-voter called: " + response);

        return ResponseEntity.ok(response);
    }
}