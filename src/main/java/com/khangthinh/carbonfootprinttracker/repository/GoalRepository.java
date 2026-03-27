package com.khangthinh.carbonfootprinttracker.repository;

import com.khangthinh.carbonfootprinttracker.entity.Goal;
import com.khangthinh.carbonfootprinttracker.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    @Query("SELECT g FROM Goal g WHERE g.user = :user AND (:status IS NULL OR g.status = :status)")
    Page<Goal> findByUserAndStatusOptional(
            @Param("user") User user,
            @Param("status") Goal.GoalStatus status,
            Pageable pageable
    );

    List<Goal> findAllByStatusAndDeadlineBefore(Goal.GoalStatus goalStatus, LocalDate now);
}
