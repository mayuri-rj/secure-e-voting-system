package com.evoting.securevoting.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User voter;

    @ManyToOne
    private Candidate candidate;

    @ManyToOne
    private Election election;

    private LocalDateTime timestamp;

    // Constructors
    public Vote() {
        this.timestamp = LocalDateTime.now(); // automatically set vote time
    }

    public Vote(User voter, Candidate candidate, Election election) {
        this.voter = voter;
        this.candidate = candidate;
        this.election = election;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and setters
    public Long getId() { return id; }
    public User getVoter() { return voter; }
    public void setVoter(User voter) { this.voter = voter; }
    public Candidate getCandidate() { return candidate; }
    public void setCandidate(Candidate candidate) { this.candidate = candidate; }
    public Election getElection() { return election; }
    public void setElection(Election election) { this.election = election; }
    public LocalDateTime getTimestamp() { return timestamp; }
}