package com.evoting.securevoting.repository;

import com.evoting.securevoting.entity.Candidate;
import com.evoting.securevoting.entity.Election;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    List<Candidate> findByElection(Election election);

}
