package com.khangthinh.carbonfootprinttracker.service;

import com.khangthinh.carbonfootprinttracker.dto.QuizResultResponse;
import com.khangthinh.carbonfootprinttracker.dto.QuizSubmitRequest;
import com.khangthinh.carbonfootprinttracker.entity.UserChallenge;

public interface UserChallengeService {
    QuizResultResponse submitQuizAttempt(String username, QuizSubmitRequest request);

    QuizResultResponse getQuizResult(String username, Long challengeId);

    void markFailedChallenges();
}
