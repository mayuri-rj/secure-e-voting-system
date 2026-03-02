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

    // Create Election (Admin Only)
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

    public List<Election> getAllElections() {
        return electionRepository.findAll();
    }
    public Election createElection(Election election) {
    return electionRepository.save(election);
}
    public Election getElectionById(Long id) {
    return electionRepository.findById(id).orElse(null);
}

public Election saveElection(Election election) {
    return electionRepository.save(election);
}
    public Election closeElection(Long electionId) {

    Optional<Election> electionOpt = electionRepository.findById(electionId);

    if (electionOpt.isEmpty()) {
        throw new RuntimeException("Election not found");
    }

    Election election = electionOpt.get();

    if (election.getStatus() == ElectionStatus.CLOSED) {
        throw new RuntimeException("Election is already closed");
    }

    election.setStatus(ElectionStatus.CLOSED);

    return electionRepository.save(election);
}
}