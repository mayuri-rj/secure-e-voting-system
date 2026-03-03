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
public class ElectionService {

    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private UserRepository userRepository;

    public Election saveElection(Election election) {
        return electionRepository.save(election);
    }

    // ✅ Create Election (Admin Only)
    public ResponseEntity<?> createElection(String adminEmail, Election election) {

        Optional<User> adminUser = userRepository.findByEmail(adminEmail);

        if (adminUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Admin not found");
        }

        if (adminUser.get().getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only ADMIN can create election");
        }

        election.setStatus(ElectionStatus.ACTIVE);

        Election savedElection = electionRepository.save(election);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedElection);
    }

    // ✅ Get All Elections
    public List<Election> getAllElections() {
        return electionRepository.findAll();
    }

    // ✅ Get Election By ID
    public Election getElectionById(Long id) {
        return electionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Election not found"));
    }

    // ✅ Close Election
    public Election closeElection(Long electionId) {

        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new RuntimeException("Election not found"));

        if (election.getStatus() == ElectionStatus.CLOSED) {
            throw new RuntimeException("Election is already closed");
        }

        election.setStatus(ElectionStatus.CLOSED);

        return electionRepository.save(election);
    }

    // ✅ Declare Result (Modified to return Candidate)
    public Candidate declareResult(Long electionId) {

        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new RuntimeException("Election not found"));

        if (election.getStatus() != ElectionStatus.CLOSED) {
            throw new RuntimeException("Election must be CLOSED to declare result");
        }

        if (election.isResultDeclared()) {
            throw new RuntimeException("Result already declared");
        }

        // 🔹 Find candidate with highest votes
        Candidate winner = election.getCandidates().stream()
                .max((c1, c2) -> Integer.compare(c1.getVoteCount(), c2.getVoteCount()))
                .orElseThrow(() -> new RuntimeException("No candidates found"));

        // 🔹 Update election
        election.setWinner(winner);
        election.setResultDeclared(true);
        electionRepository.save(election);

        // 🔹 Return only winner for controller to wrap in DTO
        return winner;
    }
}