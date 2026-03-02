package com.evoting.securevoting.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evoting.securevoting.entity.Election;

public interface ElectionRepository extends JpaRepository<Election, Long> {
}

