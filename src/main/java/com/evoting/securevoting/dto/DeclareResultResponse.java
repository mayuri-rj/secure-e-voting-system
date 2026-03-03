package com.evoting.securevoting.dto;

import com.evoting.securevoting.entity.Candidate;

public class DeclareResultResponse {

    private Candidate winner;
    private String message;

    public DeclareResultResponse(Candidate winner, String message) {
        this.winner = winner;
        this.message = message;
    }

    // Getters & Setters
    public Candidate getWinner() {
        return winner;
    }

    public void setWinner(Candidate winner) {
        this.winner = winner;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}