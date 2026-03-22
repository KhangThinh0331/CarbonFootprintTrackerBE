package com.khangthinh.carbonfootprinttracker.repository;

import com.khangthinh.carbonfootprinttracker.entity.ActivityLog;
import com.khangthinh.carbonfootprinttracker.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    Page<ActivityLog> findByUser(User user, Pageable pageable);

    @Query("SELECT SUM(a.totalCo2) FROM ActivityLog a WHERE a.user.username = :username")
    Double sumCo2ByUsername(@Param("username") String username);

    @Query("SELECT CAST(a.loggedAt AS date) as logDate, SUM(a.totalCo2) as totalCo2 " +
            "FROM ActivityLog a " +
            "WHERE a.user.username = :username AND a.loggedAt >= :startDate " +
            "GROUP BY CAST(a.loggedAt AS date) " +
            "ORDER BY CAST(a.loggedAt AS date) ASC")
    List<DailyCo2Projection> sumCo2ByDateForLast7Days(
            @Param("username") String username,
            @Param("startDate") LocalDateTime startDate
    );
}
