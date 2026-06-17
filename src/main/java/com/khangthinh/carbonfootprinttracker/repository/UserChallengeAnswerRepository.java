package com.khangthinh.carbonfootprinttracker.repository;

import com.khangthinh.carbonfootprinttracker.entity.Challenge;
import com.khangthinh.carbonfootprinttracker.entity.User;
import com.khangthinh.carbonfootprinttracker.entity.UserChallengeAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserChallengeAnswerRepository extends JpaRepository<UserChallengeAnswer,Long> {
    List<UserChallengeAnswer> findByUserAndChallenge(User user, Challenge challenge);
}
