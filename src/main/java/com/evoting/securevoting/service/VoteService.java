package com.evoting.securevoting.service;

import com.evoting.securevoting.entity.*;
import com.evoting.securevoting.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.time.LocalDateTime;

@Service
public class VoteService {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private ElectionRepository electionRepository;

    // Method to cast vote

public ResponseEntity<?> castVote(String voterEmail, Long electionId, Long candidateId) {

    System.out.println("========== DEBUG START ==========");

    if (voterEmail == null || voterEmail.trim().isEmpty()) {
        return ResponseEntity.badRequest().body("Email cannot be empty");
    }

    String cleanEmail = voterEmail.trim();
    System.out.println("Email from request: '" + cleanEmail + "'");

    Optional<User> voterOpt = userRepository.findByEmail(cleanEmail);

    System.out.println("Voter found? " + voterOpt.isPresent());

    if (voterOpt.isEmpty()) {
        return ResponseEntity.badRequest().body("Voter not found");
    }

    User voter = voterOpt.get();

    System.out.println("DB ROLE VALUE: " + voter.getRole());
    System.out.println("ROLE TYPE: " + 
        (voter.getRole() != null ? voter.getRole().getClass() : "NULL"));

    // ✅ SAFER Enum comparison
    if (!Role.VOTER.equals(voter.getRole())) {
        System.out.println("Role check failed.");
        System.out.println("========== DEBUG END ==========");
        return ResponseEntity.status(403).body("Only voters can vote");
    }

    Optional<Election> electionOpt = electionRepository.findById(electionId);
    if (electionOpt.isEmpty()) {
        System.out.println("Election not found.");
        System.out.println("========== DEBUG END ==========");
        return ResponseEntity.badRequest().body("Election not found");
    }

    Election election = electionOpt.get();

    // Check status
// Check status
if (election.getStatus() != ElectionStatus.ACTIVE) {
    return ResponseEntity.badRequest().body("Election is not active");
}

// Check date-time range
LocalDateTime now = LocalDateTime.now();

if (now.isBefore(election.getStartDate())) {
    return ResponseEntity.badRequest().body("Election has not started yet");
}

if (now.isAfter(election.getEndDate())) {
    return ResponseEntity.badRequest().body("Election has ended");
}

    Optional<Vote> existingVote = voteRepository.findByVoterAndElection(voter, election);
    if (existingVote.isPresent()) {
        System.out.println("User already voted.");
        System.out.println("========== DEBUG END ==========");
        return ResponseEntity.badRequest().body("Voter has already voted in this election");
    }

    Optional<Candidate> candidateOpt = candidateRepository.findById(candidateId);
    if (candidateOpt.isEmpty()) {
        System.out.println("Candidate not found.");
        System.out.println("========== DEBUG END ==========");
        return ResponseEntity.badRequest().body("Candidate not found");
    }

    Candidate candidate = candidateOpt.get();

    // Save vote
    Vote vote = new Vote(voter, candidate, election);
    voteRepository.save(vote);

    // Increment vote count
    candidate.setVoteCount(candidate.getVoteCount() + 1);
    candidateRepository.save(candidate);

    System.out.println("Vote successfully cast for candidate: " + candidate.getName());
    System.out.println("========== DEBUG END ==========");

    return ResponseEntity.ok("Vote cast successfully");
}
}