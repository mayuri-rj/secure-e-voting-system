package com.evoting.securevoting.controller;

import com.evoting.securevoting.service.VoteService;
import com.evoting.securevoting.repository.ElectionRepository;
import com.evoting.securevoting.repository.UserRepository;
import com.evoting.securevoting.security.JwtUtil;
import com.evoting.securevoting.entity.User;
import com.evoting.securevoting.dto.VoteRequest;
import com.evoting.securevoting.entity.Election;
import com.evoting.securevoting.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.evoting.securevoting.service.EmailService;
import com.evoting.securevoting.service.OtpService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/vote")
@CrossOrigin
public class VoteController {

    @Autowired private VoteService voteService;
    @Autowired private UserRepository userRepository;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private EmailService emailService;
    @Autowired private OtpService otpService;
    @Autowired private ElectionRepository electionRepository;

    // ✅ Cast Vote
    @PostMapping("/cast")
    public ResponseEntity<?> castVote(
            @RequestHeader("Authorization") String token,
            @RequestBody VoteRequest request) {
        try {
            String jwt = token.substring(7);
            String email = jwtUtil.extractEmail(jwt);
            return voteService.castVote(email, request.getElectionId(), request.getCandidateId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ✅ Check Voter
    @GetMapping("/check-voter")
    public ResponseEntity<?> checkVoter(@RequestParam String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty())
            return ResponseEntity.badRequest().body("User not found");

        User user = userOpt.get();
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("fullName", user.getFullName());
        response.put("email", user.getEmail());
        response.put("role", user.getRole().name());
        System.out.println("check-voter called: " + response);
        return ResponseEntity.ok(response);
    }

    // ✅ Send OTP
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(
            @RequestHeader("Authorization") String token,
            @RequestParam Long electionId) {
        try {
            String jwt = token.substring(7);
            String email = jwtUtil.extractEmail(jwt);

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Election election = electionRepository.findById(electionId)
                    .orElseThrow(() -> new RuntimeException("Election not found"));

            if (!user.getCity().equalsIgnoreCase(election.getCity())) {
                return ResponseEntity.badRequest()
                        .body("You can only vote in elections from your city");
            }

            String otp = otpService.generateOtp(email);
            emailService.sendOtpEmail(email, otp);
            return ResponseEntity.ok("OTP sent to your email");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send OTP");
        }
    }

    // ✅ Verify OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> request) {
        try {
            String jwt = token.substring(7);
            String email = jwtUtil.extractEmail(jwt);
            String otp = request.get("otp");

            boolean valid = otpService.verifyOtp(email, otp);
            if (!valid) {
                return ResponseEntity.badRequest().body("Invalid OTP");
            }

            otpService.clearOtp(email);
            return ResponseEntity.ok("OTP verified successfully");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("OTP verification failed");
        }
    }

    // ✅ Check if voter has already voted in this election
    @GetMapping("/has-voted")
    public ResponseEntity<?> hasVoted(
            @RequestHeader("Authorization") String token,
            @RequestParam Long electionId) {
        try {
            String jwt = token.substring(7);
            String email = jwtUtil.extractEmail(jwt);
            boolean alreadyVoted = voteService.hasVoted(email, electionId);
            return ResponseEntity.ok(Map.of("hasVoted", alreadyVoted));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error checking vote status");
        }
    }
}