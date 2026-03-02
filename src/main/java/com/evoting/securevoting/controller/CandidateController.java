package com.evoting.securevoting.controller;

import com.evoting.securevoting.entity.Candidate;
import com.evoting.securevoting.service.CandidateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/candidates")
@CrossOrigin
public class CandidateController {

    @Autowired
    private CandidateService candidateService;

    // ✅ Add Candidate (Admin Only)
    @PostMapping("/add")
    public ResponseEntity<?> addCandidate(@RequestParam String adminEmail,
                                          @RequestParam Long electionId,
                                          @RequestBody Candidate candidate) {

        return candidateService.addCandidate(adminEmail, electionId, candidate);
    }

    // ✅ Get Candidates By Election
    @GetMapping("/by-election")
    public ResponseEntity<?> getCandidatesByElection(@RequestParam Long electionId) {

        return candidateService.getCandidatesByElection(electionId);
    }
    @GetMapping("/test")
public String test() {
    return "CandidateController works!";
}
}
