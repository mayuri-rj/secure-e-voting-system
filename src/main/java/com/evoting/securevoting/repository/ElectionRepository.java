package com.evoting.securevoting.repository;

import com.evoting.securevoting.entity.Election;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ElectionRepository extends JpaRepository<Election, Long> {

    boolean existsByTitle(String title);

    // ✅ FIX for getAllElections: DISTINCT prevents duplicate Election objects
    // when LEFT JOIN produces multiple rows per election (one per candidate)
    @Query("SELECT DISTINCT e FROM Election e LEFT JOIN FETCH e.candidates")
    List<Election> findAllWithCandidates();

    // ✅ FIX for results endpoint: eagerly load candidates for a single election
    @Query("SELECT e FROM Election e LEFT JOIN FETCH e.candidates WHERE e.id = :id")
    Optional<Election> findByIdWithCandidates(@Param("id") Long id);
}