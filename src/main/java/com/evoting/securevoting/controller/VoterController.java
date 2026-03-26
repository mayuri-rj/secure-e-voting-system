package com.evoting.securevoting.controller;

import com.evoting.securevoting.dto.ProfileDTO;
import com.evoting.securevoting.service.VoterService;
import com.evoting.securevoting.security.JwtUtil;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/voter")
public class VoterController {

    private final VoterService voterService;
    private final JwtUtil jwtUtil;

    public VoterController(VoterService voterService, JwtUtil jwtUtil) {
        this.voterService = voterService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(
            @RequestHeader("Authorization") String token) {

        String email = jwtUtil.extractEmail(token.substring(7));

        ProfileDTO profile = voterService.getProfile(email);

        

        return ResponseEntity.ok(profile);
    }
}
