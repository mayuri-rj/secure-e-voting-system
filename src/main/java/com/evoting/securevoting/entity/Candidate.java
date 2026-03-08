package com.evoting.securevoting.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@Table(name = "candidates")
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String party;

    private int voteCount = 0;

    @ManyToOne
@JoinColumn(name = "election_id")
@JsonIgnoreProperties("candidates") // prevents infinite recursion
@JsonBackReference
private Election election;

   

    // Constructors
    public Candidate() {}

    public Candidate(String name, String party, Election election) {
        this.name = name;
        this.party = party;
        this.election = election;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public Election getElection() {
        return election;
    }

    public void setElection(Election election) {
        this.election = election;
    }
}