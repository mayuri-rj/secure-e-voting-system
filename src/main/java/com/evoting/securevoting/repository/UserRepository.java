package com.evoting.securevoting.repository;

import com.evoting.securevoting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import com.evoting.securevoting.entity.Role;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByAadhaarNumber(String aadhaarNumber);

    List<User> findByVerifiedFalse();

    List<User> findByVerifiedFalseAndRole(Role role);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE User u SET u.hasVoted = true WHERE u.id = :id")
    void markUserAsVoted(@Param("id") Long id);

}