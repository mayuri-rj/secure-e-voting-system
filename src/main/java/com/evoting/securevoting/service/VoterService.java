package com.evoting.securevoting.service;

import org.springframework.stereotype.Service;

import com.evoting.securevoting.dto.ProfileDTO;
import com.evoting.securevoting.entity.Election;
import com.evoting.securevoting.entity.Vote;
import com.evoting.securevoting.repository.UserRepository;
import com.evoting.securevoting.repository.VoteRepository;
import com.evoting.securevoting.entity.User;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class VoterService {

    private final UserRepository userRepository;
    private final VoteRepository voteRepository;

    public VoterService(UserRepository userRepository, VoteRepository voteRepository) {
        this.userRepository = userRepository;
        this.voteRepository = voteRepository;
    }

    public ProfileDTO getProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int age = 0;
        if (user.getDob() != null) {
            age = Period.between(user.getDob(), LocalDate.now()).getYears();
        }

        String ageGender = age + " / " + (user.getGender() != null ? user.getGender() : "N/A");

        // Mask Aadhaar
        String maskedAadhaar = "Not Provided";
        if (user.getAadhaarNumber() != null && user.getAadhaarNumber().length() >= 4) {
            String last4 = user.getAadhaarNumber()
                    .substring(user.getAadhaarNumber().length() - 4);
            maskedAadhaar = "********" + last4;
        }

        String electionTitle = null;
        String electionCity = null;

        // ✅ Use findAllByVoter to handle voters who voted in multiple elections
        if (user.isHasVoted()) {
            List<Vote> votes = voteRepository.findAllByVoter(user);
            if (!votes.isEmpty()) {
                Vote latestVote = votes.get(votes.size() - 1);
                Election election = latestVote.getElection();
                electionTitle = election.getTitle();
                electionCity = election.getCity();
            }
        }

        // ✅ Return statement was missing — added back
        return new ProfileDTO(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getCity(),
                user.getDob() != null ? user.getDob().toString() : "Not Provided",
                ageGender,
                maskedAadhaar,
                user.isVerified(),
                user.isHasVoted(),
                electionTitle,
                electionCity);
    }
}