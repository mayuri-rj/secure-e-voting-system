package com.evoting.securevoting.service;

import com.evoting.securevoting.entity.*;
import com.evoting.securevoting.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.Optional;
import java.time.LocalDateTime;

@Service
@Transactional
public class VoteService {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private ElectionRepository electionRepository;

    // ✅ Cast Vote
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

        // Check if voter is verified
        if (!voter.isVerified()) {
            return ResponseEntity.status(403).body("You are not verified by admin");
        }

        System.out.println("DB ROLE VALUE: " + voter.getRole());

        // Role check
        if (!Role.VOTER.equals(voter.getRole())) {
            System.out.println("Role check failed.");
            return ResponseEntity.status(403).body("Only voters can vote");
        }

        Optional<Election> electionOpt = electionRepository.findById(electionId);
        if (electionOpt.isEmpty()) {
            System.out.println("Election not found.");
            return ResponseEntity.badRequest().body("Election not found");
        }
        Election election = electionOpt.get();

        // ✅ Dynamic status check from dates (NOT from DB status field)
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(election.getStartDate())) {
            return ResponseEntity.badRequest().body("Election has not started yet");
        }
        if (now.isAfter(election.getEndDate())) {
            return ResponseEntity.badRequest().body("Election has ended");
        }

        // ✅ City validation
        if (!voter.getCity().equalsIgnoreCase(election.getCity())) {
            return ResponseEntity.badRequest().body("You can only vote in elections from your city");
        }

        // ✅ Check already voted
        Optional<Vote> existingVote = voteRepository.findByVoterAndElection(voter, election);
        if (existingVote.isPresent()) {
            System.out.println("User already voted.");
            return ResponseEntity.badRequest().body("Voter has already voted in this election");
        }

        Optional<Candidate> candidateOpt = candidateRepository.findById(candidateId);
        if (candidateOpt.isEmpty()) {
            System.out.println("Candidate not found.");
            return ResponseEntity.badRequest().body("Candidate not found");
        }

        Candidate candidate = candidateOpt.get();

        // ✅ Save vote
        Vote vote = new Vote(voter, candidate, election);
        voteRepository.save(vote);

        // ✅ Increase candidate vote count
        candidate.setVoteCount(candidate.getVoteCount() + 1);
        candidateRepository.save(candidate);

        // ✅ Mark user as voted
        userRepository.markUserAsVoted(voter.getId());

        System.out.println("Vote successfully cast for candidate: " + candidate.getName());
        System.out.println("========== DEBUG END ==========");

        return ResponseEntity.ok("Vote cast successfully");
    }

    // ✅ Check if voter has already voted in this election
    public boolean hasVoted(String voterEmail, Long electionId) {
        Optional<User> voterOpt = userRepository.findByEmail(voterEmail.trim());
        if (voterOpt.isEmpty()) return false;

        Optional<Election> electionOpt = electionRepository.findById(electionId);
        if (electionOpt.isEmpty()) return false;

        return voteRepository.findByVoterAndElection(voterOpt.get(), electionOpt.get()).isPresent();
    }
}