package com.evoting.securevoting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import com.evoting.securevoting.entity.Election;

import java.util.List;
public interface ElectionRepository extends JpaRepository<Election, Long> {
    @EntityGraph(attributePaths = {"candidates"})
    List<Election> findAll();  // This will fetch elections AND candidates in one go

}

