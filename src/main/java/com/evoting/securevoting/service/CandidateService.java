package com.evoting.securevoting.service;

import com.evoting.securevoting.entity.*;
import com.evoting.securevoting.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

@Service
public class CandidateService {

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private UserRepository userRepository;

    // ✅ Add Candidate (Admin Only)
    public ResponseEntity<?> addCandidate(String adminEmail, Long electionId, Candidate candidate) {

        Optional<User> adminUser = userRepository.findByEmail(adminEmail);

        if (adminUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Admin not found");
        }

        if (adminUser.get().getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only ADMIN can add candidates");
        }

        Optional<Election> electionOptional = electionRepository.findById(electionId);

        if (electionOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Election not found");
        }

        candidate.setElection(electionOptional.get());
        candidate.setVoteCount(0);

        Candidate savedCandidate = candidateRepository.save(candidate);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedCandidate);
    }

    // ✅ Get Candidates by Election
    public ResponseEntity<?> getCandidatesByElection(Long electionId) {

        Optional<Election> electionOptional = electionRepository.findById(electionId);

        if (electionOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Election not found");
        }

        List<Candidate> candidates =
                candidateRepository.findByElection(electionOptional.get());

        return ResponseEntity.ok(candidates);
    }
}