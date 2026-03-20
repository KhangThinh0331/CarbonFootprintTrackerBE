package com.khangthinh.carbonfootprinttracker.service;

import com.khangthinh.carbonfootprinttracker.entity.UserChallenge;

public interface UserChallengeService {
    UserChallenge joinChallenge(String username, Long challengeId);

    void completeChallenge(Long userId, Long challengeId);

    void markFailedChallenges();
}
