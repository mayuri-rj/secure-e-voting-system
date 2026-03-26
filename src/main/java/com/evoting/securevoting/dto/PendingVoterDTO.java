package com.evoting.securevoting.dto;

public class PendingVoterDTO {

    private Long id;
    private String fullName;
    private String email;
    private String city;
    private boolean verified;

    public PendingVoterDTO(Long id, String fullName, String email, String city, boolean verified) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.city = city;
        this.verified = verified;
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getCity() { return city; }
    public boolean isVerified() { return verified; }
}