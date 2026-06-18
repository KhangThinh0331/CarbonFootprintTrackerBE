package com.khangthinh.carbonfootprinttracker.service.impl;

import com.khangthinh.carbonfootprinttracker.entity.Answer;
import com.khangthinh.carbonfootprinttracker.entity.Challenge;
import com.khangthinh.carbonfootprinttracker.entity.Question;
import com.khangthinh.carbonfootprinttracker.repository.ChallengeRepository;
import com.khangthinh.carbonfootprinttracker.service.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChallengeServiceImpl implements ChallengeService {
    private final ChallengeRepository challengeRepository;

    @Override
    public Page<Challenge> getAllChallenges(Pageable pageable) {
        return challengeRepository.findAll(pageable);
    }

    @Override
    public Challenge createChallenge(Challenge challenge) {
        if (challenge.getQuestions() != null) {
            for (Question question : challenge.getQuestions()) {
                question.setChallenge(challenge);

                if (question.getAnswers() != null) {
                    for (Answer answer : question.getAnswers()) {
                        answer.setQuestion(question);
                    }
                }
            }
        }
        return challengeRepository.save(challenge);
    }
}
