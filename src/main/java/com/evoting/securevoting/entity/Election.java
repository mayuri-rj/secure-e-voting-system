package com.evoting.securevoting.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "elections")
public class Election {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private ElectionStatus status = ElectionStatus.UPCOMING;

    // ✅ FIX: Replace @JsonManagedReference with @JsonIgnoreProperties
    // @JsonManagedReference works with @JsonBackReference but suppresses fields
    // @JsonIgnoreProperties breaks the loop cleanly while keeping all fields serialized
    @OneToMany(mappedBy = "election", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"election"})
    private List<Candidate> candidates;

    // ✅ FIX: winner field also needs to ignore election back-reference
    // Without this, serializing winner → election → candidates → winner = infinite loop = 500
    @ManyToOne
    @JoinColumn(name = "winner_id")
    @JsonIgnoreProperties({"election", "candidates"})
    private Candidate winner;

    @Column(nullable = false)
    private boolean resultDeclared = false;

    @Column(nullable = false)
    private String city;

    // Constructors
    public Election() {}

    public Election(String title, LocalDateTime startDate, LocalDateTime endDate, ElectionStatus status) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.resultDeclared = false;
    }

    // Getters & Setters
    public Long getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public ElectionStatus getStatus() { return status; }
    public void setStatus(ElectionStatus status) { this.status = status; }

    public List<Candidate> getCandidates() { return candidates; }
    public void setCandidates(List<Candidate> candidates) { this.candidates = candidates; }

    public Candidate getWinner() { return winner; }
    public void setWinner(Candidate winner) { this.winner = winner; }

    public boolean isResultDeclared() { return resultDeclared; }
    public void setResultDeclared(boolean resultDeclared) { this.resultDeclared = resultDeclared; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
}