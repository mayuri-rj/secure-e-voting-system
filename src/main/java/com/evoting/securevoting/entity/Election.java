package com.evoting.securevoting.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "elections")
public class Election {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private ElectionStatus status;

    @ManyToOne
@JoinColumn(name = "winner_id")
private Candidate winner;
@Column(nullable = false)
private boolean resultDeclared = false;

    // Constructors
    public Election() {
    }

    public Election(String title, LocalDateTime startDate, LocalDateTime endDate, ElectionStatus status) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public ElectionStatus getStatus() {
        return status;
    }

    public void setStatus(ElectionStatus status) {
        this.status = status;
    }

    public boolean isResultDeclared() {
    return resultDeclared;
}

public void setResultDeclared(boolean resultDeclared) {
    this.resultDeclared = resultDeclared;
}
}
