package com.evoting.securevoting.controller;

import com.evoting.securevoting.entity.Election;
import com.evoting.securevoting.entity.ElectionStatus;
import com.evoting.securevoting.service.ElectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/elections")
@CrossOrigin
public class ElectionController {

    @Autowired
    private ElectionService electionService;

    // Create Election
   @PostMapping("/create")
public ResponseEntity<Election> createElection(@RequestBody Election election) {

    election.setStatus(ElectionStatus.UPCOMING);

    return ResponseEntity.ok(electionService.createElection(election));
}

    // Get All Elections
    @GetMapping("/all")
    public List<Election> getAllElections() {
        return electionService.getAllElections();
    }
  @PutMapping("/close/{id}")
public ResponseEntity<?> closeElection(@PathVariable Long id,
                                       @RequestParam(required = false) Boolean confirm) {
    // Check confirm parameter
    if (confirm == null || !confirm) {
        return ResponseEntity.badRequest()
                .body("Are you sure you want to close this election? Pass confirm=true to proceed.");
    }

    // Fetch election
    Election election = electionService.getElectionById(id);
    if (election == null) {
        return ResponseEntity.badRequest().body("Election not found");
    }

    // Check if already closed
    if (election.getStatus() == ElectionStatus.CLOSED) {
        return ResponseEntity.badRequest().body("Election is already closed");
    }

    // Close election
    election.setStatus(ElectionStatus.CLOSED);
    electionService.saveElection(election);

    return ResponseEntity.ok(election);
}
}