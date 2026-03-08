package com.evoting.securevoting.dto;
import jakarta.validation.constraints.*;


public class RegisterRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$",
        message = "Password must be at least 8 characters, contain 1 uppercase letter and 1 number"
    )
    private String password;

    private String role;

    // getters setters
}