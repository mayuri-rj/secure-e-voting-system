package com.evoting.securevoting.service;

import com.evoting.securevoting.entity.*;
import com.evoting.securevoting.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ElectionService {

    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private UserRepository userRepository;

    public Election saveElection(Election election) {
        return electionRepository.save(election);
    }

    // ✅ Create Election
    public ResponseEntity<?> createElection(String adminEmail, Election election) {
        Optional<User> adminUser = userRepository.findByEmail(adminEmail);
        if (adminUser.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found");
        if (adminUser.get().getRole() != Role.ADMIN) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only ADMIN can create election");
        election.setStatus(ElectionStatus.ACTIVE);
        return ResponseEntity.status(HttpStatus.CREATED).body(electionRepository.save(election));
    }

    // ✅ Get All Elections — uses DISTINCT JOIN FETCH to avoid duplicate elections
    // and to eagerly load candidates (with their voteCount) in one query
    public List<Election> getAllElections() {
        return electionRepository.findAllWithCandidates();
    }

    // ✅ Get Election By ID — simple (used internally, no candidates needed)
    public Election getElectionById(Long id) {
        return electionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Election not found"));
    }

    // ✅ Get Election By ID WITH candidates eagerly loaded — used for results endpoint
    public Election getElectionByIdWithCandidates(Long id) {
        return electionRepository.findByIdWithCandidates(id)
                .orElseThrow(() -> new RuntimeException("Election not found"));
    }

    public boolean existsByTitle(String title) {
        return electionRepository.existsByTitle(title);
    }

    // ✅ Close Election
    public Election closeElection(Long electionId) {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new RuntimeException("Election not found"));
        if (election.getStatus() == ElectionStatus.CLOSED) throw new RuntimeException("Election is already closed");
        election.setStatus(ElectionStatus.CLOSED);
        return electionRepository.save(election);
    }

    // ✅ Declare Result
    public Candidate declareResult(Long electionId) {
        Election election = electionRepository.findByIdWithCandidates(electionId)
                .orElseThrow(() -> new RuntimeException("Election not found"));

        LocalDateTime now = LocalDateTime.now();
        if (election.getEndDate().isAfter(now)) {
            throw new RuntimeException("Election has not ended yet. It ends on " + election.getEndDate());
        }
        if (election.isResultDeclared()) throw new RuntimeException("Result already declared");

        Candidate winner = election.getCandidates().stream()
                .max((c1, c2) -> Integer.compare(c1.getVoteCount(), c2.getVoteCount()))
                .orElseThrow(() -> new RuntimeException("No candidates found for this election"));

        election.setWinner(winner);
        election.setResultDeclared(true);
        election.setStatus(ElectionStatus.CLOSED);
        electionRepository.save(election);

        return winner;
    }
}