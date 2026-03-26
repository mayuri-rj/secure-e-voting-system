package com.evoting.securevoting.dto;

public class ProfileDTO {

    private Long id;
    private String fullName;
    private String email;
    private String city;
    private String dob;
    private String ageGender;
    private String aadhaarMasked;
    private boolean verified;
    private boolean hasVoted;
    private String electionTitle;
    private String electionCity;

    public ProfileDTO(Long id, String fullName, String email, String city,
            String dob, String ageGender, String aadhaarMasked,
            boolean verified, boolean hasVoted,
            String electionTitle, String electionCity) {

        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.city = city;
        this.dob = dob;
        this.ageGender = ageGender;
        this.aadhaarMasked = aadhaarMasked;
        this.verified = verified;
        this.hasVoted = hasVoted;
        this.electionTitle = electionTitle;
        this.electionCity = electionCity;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getCity() {
        return city;
    }

    public String getDob() {
        return dob;
    }

    public String getAgeGender() {
        return ageGender;
    }

    public String getAadhaarMasked() {
        return aadhaarMasked;
    }

    public boolean isVerified() {
        return verified;
    }

    public boolean isHasVoted() {
        return hasVoted;
    }

    public String getElectionTitle() {
        return electionTitle;
    }

    public String getElectionCity() {
        return electionCity;
    }
}