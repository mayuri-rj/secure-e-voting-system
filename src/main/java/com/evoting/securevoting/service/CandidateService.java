package com.evoting.securevoting.service;

import com.evoting.securevoting.entity.*;
import com.evoting.securevoting.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.evoting.securevoting.dto.CandidateDTO;

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


     public CandidateDTO createCandidate(Candidate candidate, Long electionId) {
    // fetch election
    Election election = electionRepository.findById(electionId)
            .orElseThrow(() -> new RuntimeException("Election not found"));

    // Check if candidate with same name already exists in this election
    boolean exists = candidateRepository.existsByNameAndElectionId(candidate.getName(), electionId);
    if (exists) {
        throw new RuntimeException("Candidate with this name already exists in this election");
    }

    // set election for candidate
    candidate.setElection(election);
    candidate.setParty(election.getTitle());

    // save candidate
    Candidate savedCandidate = candidateRepository.save(candidate);

    // return DTO
    return new CandidateDTO(savedCandidate);
}


     // ✅ Create candidate
    public Candidate createCandidate(Candidate candidate) {
        return candidateRepository.save(candidate);
    }

    // ✅ Get all candidates
    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    // ✅ Add Candidate (Admin Only)
    public ResponseEntity<?> addCandidate(String adminEmail, Long electionId, Candidate candidate) {

        Optional<User> adminUser = userRepository.findByEmail(adminEmail);

        if (adminUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Admin not found");
        }

       if (adminUser.get().getRole() != Role.ADMIN){
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
