package com.khangthinh.carbonfootprinttracker.repository;

import com.khangthinh.carbonfootprinttracker.dto.UserChallengeId;
import com.khangthinh.carbonfootprinttracker.entity.Challenge;
import com.khangthinh.carbonfootprinttracker.entity.User;
import com.khangthinh.carbonfootprinttracker.entity.UserChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface UserChallengeRepository extends JpaRepository<UserChallenge, UserChallengeId> {
    boolean existsByUserAndChallenge(User user, Challenge challenge);

    @Modifying
    @Query("UPDATE UserChallenge uc " +
            "SET uc.status = com.khangthinh.carbonfootprinttracker.entity.UserChallenge.ChallengeStatus.FAILED " +
            "WHERE uc.status = com.khangthinh.carbonfootprinttracker.entity.UserChallenge.ChallengeStatus.IN_PROGRESS " +
            "AND uc.challenge.endDate < :now")
    int updateFailedChallenges(@Param("now") LocalDate now);
}
