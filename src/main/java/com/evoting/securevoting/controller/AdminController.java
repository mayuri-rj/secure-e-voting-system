package com.evoting.securevoting.controller;

import com.evoting.securevoting.dto.PendingVoterDTO;
import com.evoting.securevoting.entity.User;
import com.evoting.securevoting.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.evoting.securevoting.repository.*;
import com.evoting.securevoting.service.*;
import org.springframework.stereotype.Service;
import com.evoting.securevoting.service.AdminService;

import java.util.List;

@RestController
@RequestMapping("/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/pending-voters")
    public ResponseEntity<List<PendingVoterDTO>> getPendingVoters() {
        return ResponseEntity.ok(adminService.getPendingVoters());
    }

    @PutMapping("/approve-voter/{id}")
    public ResponseEntity<String> approveVoter(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.approveVoter(id));
    }

    @DeleteMapping("/reject-voter/{id}")
    public ResponseEntity<String> rejectVoter(@PathVariable Long id) {

        return ResponseEntity.ok(adminService.rejectVoter(id));

    }
}