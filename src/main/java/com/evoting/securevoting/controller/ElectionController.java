package com.evoting.securevoting.controller;

import com.evoting.securevoting.dto.DeclareResultResponse;
import com.evoting.securevoting.entity.Candidate;
import com.evoting.securevoting.entity.Election;
import com.evoting.securevoting.entity.ElectionStatus;
import com.evoting.securevoting.entity.Role;
import com.evoting.securevoting.entity.User;
import com.evoting.securevoting.security.JwtUtil;
import com.evoting.securevoting.service.ElectionService;
import com.evoting.securevoting.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/elections")
@CrossOrigin

public class ElectionController {

    @Autowired
    private ElectionService electionService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    // ✅ Create Election
   @PostMapping("/create")
public ResponseEntity<?> createElection(@RequestBody Election election) {
    try {
        // 1️⃣ Check if election with same title already exists
        if (electionService.existsByTitle(election.getTitle())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Election already exists!");
        }

        // 2️⃣ Set status based on current date/time
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(election.getStartDate())) {
            election.setStatus(ElectionStatus.UPCOMING);
        } else if (now.isAfter(election.getEndDate())) {
            election.setStatus(ElectionStatus.CLOSED);
        } else {
            election.setStatus(ElectionStatus.ACTIVE);
        }

        // 3️⃣ Save election
        Election savedElection = electionService.saveElection(election);
        return ResponseEntity.ok(savedElection);

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error creating election: " + e.getMessage());
    }
}

    // ✅ Get All Elections
    @GetMapping("/all")
    public List<Election> getAllElections() {
        List<Election> elections = electionService.getAllElections();
        LocalDateTime now = LocalDateTime.now();

        for (Election e : elections) {
            if (e.getStartDate().isAfter(now)) {
                e.setStatus(ElectionStatus.UPCOMING);
            } else if (e.getEndDate().isBefore(now)) {
                e.setStatus(ElectionStatus.CLOSED);
            } else {
                e.setStatus(ElectionStatus.ACTIVE);
            }
        }

        return elections;
    }

    // ✅ Close Election
    @PutMapping("/close/{id}")
    public ResponseEntity<?> closeElection(@PathVariable Long id,
            @RequestParam(required = false) Boolean confirm) {
        if (confirm == null || !confirm) {
            return ResponseEntity.badRequest()
                    .body("Are you sure you want to close this election? Pass confirm=true to proceed.");
        }

        try {
            Election election = electionService.closeElection(id); // use service method
            return ResponseEntity.ok(election);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // ✅ Declare Result (Admin Only) — Clean version
    @PutMapping("/declare-result/{id}")
    public ResponseEntity<?> declareResult(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        try {
            // 🔹 Extract admin user from JWT
            String jwt = token.substring(7);
            String email = jwtUtil.extractEmail(jwt);

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (user.getRole() != Role.ADMIN) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Only ADMIN can declare result");
            }

            // 🔹 Call service to declare result (returns winner Candidate)
            Candidate winner = electionService.declareResult(id);

            // 🔹 Wrap in DTO for clean JSON format
            DeclareResultResponse response = new DeclareResultResponse(winner, "Result declared successfully");

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // 🔹 Handles "Result already declared" and other runtime exceptions
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong");
        }

    }

    // ✅ View Results
    @GetMapping("/results/{id}")
public Election getElectionResults(@PathVariable Long id) {
    return electionService.getElectionByIdWithCandidates(id);
}
}
