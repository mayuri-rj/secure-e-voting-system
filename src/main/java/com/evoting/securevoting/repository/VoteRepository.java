package com.evoting.securevoting.repository;

import com.evoting.securevoting.entity.Vote;
import com.evoting.securevoting.entity.User;
import com.evoting.securevoting.entity.Election;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    // Check if voter already voted in this election
    Optional<Vote> findByVoterAndElection(User voter, Election election);
    List<Vote> findAllByVoter(User voter);
}