package com.khangthinh.carbonfootprinttracker.service;

import com.khangthinh.carbonfootprinttracker.entity.Challenge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChallengeService {
    Page<Challenge> getAllChallenges(Pageable pageable);

    Challenge createChallenge(Challenge challenge);
}
