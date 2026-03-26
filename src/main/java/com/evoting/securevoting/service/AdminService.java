package com.evoting.securevoting.service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evoting.securevoting.repository.*;
import org.springframework.stereotype.Service;

import com.evoting.securevoting.dto.PendingVoterDTO;
import com.evoting.securevoting.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    public String approveVoter(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setVerified(true);

        userRepository.save(user);

        return "Voter approved successfully";
    }

    public String rejectVoter(Long id) {

        userRepository.deleteById(id);

        return "Voter rejected and removed";
    }
public List<PendingVoterDTO> getPendingVoters() {

    List<User> users = userRepository.findByVerifiedFalseAndRole(Role.VOTER);

    return users.stream()
            .map(user -> new PendingVoterDTO(
                    user.getId(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getCity(),
                    user.isVerified()))
            .toList();
}
}