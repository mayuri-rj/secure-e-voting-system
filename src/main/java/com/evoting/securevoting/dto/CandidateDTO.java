package com.evoting.securevoting.dto;

import com.evoting.securevoting.entity.Candidate;
import com.evoting.securevoting.entity.Election;

public class CandidateDTO {
    private Long id;
    private String name;
    private String party;
    private int voteCount;
    private ElectionInfo election;

    // Constructor to convert Candidate entity to DTO
    public CandidateDTO(Candidate candidate) {
        this.id = candidate.getId();
        this.name = candidate.getName();
        this.party = candidate.getParty();
        this.voteCount = candidate.getVoteCount();
        if (candidate.getElection() != null) {
            this.election = new ElectionInfo(candidate.getElection());
        }
    }

    // Getters & Setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getParty() { return party; }
    public int getVoteCount() { return voteCount; }
    public ElectionInfo getElection() { return election; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setParty(String party) { this.party = party; }
    public void setVoteCount(int voteCount) { this.voteCount = voteCount; }
    public void setElection(ElectionInfo election) { this.election = election; }

    // Nested class for Election info
    public static class ElectionInfo {
        private Long id;
        private String title;

        public ElectionInfo(Election election) {
            this.id = election.getId();
            this.title = election.getTitle();
        }

        // Getters & Setters
        public Long getId() { return id; }
        public String getTitle() { return title; }
        public void setId(Long id) { this.id = id; }
        public void setTitle(String title) { this.title = title; }
    }
}