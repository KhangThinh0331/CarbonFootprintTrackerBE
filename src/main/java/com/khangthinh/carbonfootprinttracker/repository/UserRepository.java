package com.khangthinh.carbonfootprinttracker.repository;

import com.khangthinh.carbonfootprinttracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    void deleteByIsActiveFalseAndCreatedAtBefore(LocalDateTime dateTime);
}
