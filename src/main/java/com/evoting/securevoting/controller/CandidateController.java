package com.evoting.securevoting.controller;

import com.evoting.securevoting.entity.Candidate;
import com.evoting.securevoting.security.JwtUtil;
import com.evoting.securevoting.service.CandidateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/candidates")
@CrossOrigin
public class CandidateController {

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private JwtUtil jwtUtil;

    // ✅ Add Candidate (Admin Only)
    @PostMapping("/add")
    public ResponseEntity<?> addCandidate(
            @RequestHeader("Authorization") String token,
            @RequestParam Long electionId,
            @RequestBody Candidate candidate) {

        try {
            String jwt = token.substring(7);
            String email = jwtUtil.extractEmail(jwt);

            return candidateService.addCandidate(email, electionId, candidate);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // ✅ Get candidates by election
    @GetMapping("/by-election/{electionId}")
    public ResponseEntity<?> getCandidates(@PathVariable Long electionId) {
        return candidateService.getCandidatesByElection(electionId);
    }
    @GetMapping("/test")
public String test() {
    return "CandidateController works!";
}
}
