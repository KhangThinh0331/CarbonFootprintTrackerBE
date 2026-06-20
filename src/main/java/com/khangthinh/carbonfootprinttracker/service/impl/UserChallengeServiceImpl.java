package com.khangthinh.carbonfootprinttracker.service.impl;

import com.khangthinh.carbonfootprinttracker.dto.QuizResultResponse;
import com.khangthinh.carbonfootprinttracker.dto.QuizSubmitRequest;
import com.khangthinh.carbonfootprinttracker.entity.*;
import com.khangthinh.carbonfootprinttracker.repository.*;
import com.khangthinh.carbonfootprinttracker.service.UserChallengeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserChallengeServiceImpl implements UserChallengeService {
    private final UserChallengeRepository userChallengeRepository;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final UserChallengeAnswerRepository userChallengeAnswerRepository;
    private final AnswerRepository answerRepository;

    @Transactional
    @Override
    public QuizResultResponse submitQuizAttempt(String username, QuizSubmitRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        Challenge challenge = challengeRepository.findById(request.getChallengeId())
                .orElseThrow(() -> new RuntimeException("Thử thách không tồn tại"));

        LocalDate now = LocalDate.now();
        if (challenge.getStartDate() != null && now.isBefore(challenge.getStartDate())) {
            throw new RuntimeException("Thử thách chưa tới ngày bắt đầu!");
        }
        if (challenge.getEndDate() != null && now.isAfter(challenge.getEndDate())) {
            throw new RuntimeException("Thử thách đã kết thúc, không thể tham gia!");
        }

        if (userChallengeRepository.existsByUserAndChallenge(user, challenge)) {
            throw new RuntimeException("Bạn đã thực hiện thử thách này rồi. Mỗi người chỉ được làm 1 lần!");
        }

        int correctCount = 0;
        List<QuizResultResponse.QuestionResult> resultDetails = new ArrayList<>();
        List<Question> challengeQuestions = challenge.getQuestions();
        List<UserChallengeAnswer> userAnswersToSave = new ArrayList<>();

        for (QuizSubmitRequest.AnswerItem submittedAnswer : request.getAnswers()) {
            Question question = challengeQuestions.stream()
                    .filter(q -> q.getId().equals(submittedAnswer.getQuestionId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Câu hỏi không thuộc thử thách này"));

            Answer correctAnswer = question.getAnswers().stream()
                    .filter(Answer::isCorrect)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Câu hỏi bị lỗi thiếu đáp án đúng"));

            boolean isCorrect = correctAnswer.getId().equals(submittedAnswer.getSelectedAnswerId());
            if (isCorrect) {
                correctCount++;
            }

            Answer selectedAnswer = answerRepository.findById(submittedAnswer.getSelectedAnswerId())
                    .orElseThrow(() -> new RuntimeException("Đáp án đã chọn không tồn tại"));

            userAnswersToSave.add(UserChallengeAnswer.builder()
                    .user(user)
                    .challenge(challenge)
                    .question(question)
                    .selectedAnswer(selectedAnswer)
                    .build());

            resultDetails.add(QuizResultResponse.QuestionResult.builder()
                    .questionId(question.getId())
                    .selectedAnswerId(submittedAnswer.getSelectedAnswerId())
                    .correctAnswerId(correctAnswer.getId())
                    .isCorrect(isCorrect)
                    .build());
        }

        userChallengeAnswerRepository.saveAll(userAnswersToSave);

        boolean isPassed = (correctCount == challengeQuestions.size());
        UserChallenge userChallenge = UserChallenge.builder()
                .user(user)
                .challenge(challenge)
                .status(isPassed ? UserChallenge.ChallengeStatus.COMPLETED : UserChallenge.ChallengeStatus.FAILED)
                .build();
        userChallengeRepository.save(userChallenge);

        int pointsToEarn = 0;
        if (isPassed) {
            pointsToEarn = challenge.getPoints();
            int currentPoints = (user.getTotalPoints() != null) ? user.getTotalPoints() : 0;
            user.setTotalPoints(currentPoints + pointsToEarn);
            userRepository.save(user);
        }

        return QuizResultResponse.builder()
                .isPassed(isPassed)
                .score(correctCount)
                .totalQuestions(challengeQuestions.size())
                .pointsEarned(pointsToEarn)
                .details(resultDetails)
                .build();
    }

    @Override
    public QuizResultResponse getQuizResult(String username, Long challengeId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Thử thách không tồn tại"));
        UserChallenge userChallenge = userChallengeRepository.findByUserAndChallenge(user, challenge)
                .orElseThrow(() -> new RuntimeException("Bạn chưa tham gia thử thách này!"));

        List<UserChallengeAnswer> savedAnswers = userChallengeAnswerRepository.findByUserAndChallenge(user, challenge);

        List<QuizResultResponse.QuestionResult> resultDetails = new ArrayList<>();
        int correctCount = 0;

        for (UserChallengeAnswer savedAns : savedAnswers) {
            Answer correctAnswer = savedAns.getQuestion().getAnswers().stream()
                    .filter(Answer::isCorrect)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Câu hỏi thiếu đáp án đúng"));

            boolean isCorrect = savedAns.getSelectedAnswer().getId().equals(correctAnswer.getId());
            if (isCorrect) {
                correctCount++;
            }

            resultDetails.add(QuizResultResponse.QuestionResult.builder()
                    .questionId(savedAns.getQuestion().getId())
                    .selectedAnswerId(savedAns.getSelectedAnswer().getId())
                    .correctAnswerId(correctAnswer.getId())
                    .isCorrect(isCorrect)
                    .build());
        }

        boolean isPassed = (userChallenge.getStatus() == UserChallenge.ChallengeStatus.COMPLETED);

        return QuizResultResponse.builder()
                .isPassed(isPassed)
                .score(correctCount)
                .totalQuestions(challenge.getQuestions().size())
                .pointsEarned(isPassed ? challenge.getPoints() : 0)
                .details(resultDetails)
                .build();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    @Override
    public void markFailedChallenges() {
        LocalDate today = LocalDate.now();

        int updatedCount = userChallengeRepository.updateFailedChallenges(today);

        System.out.println("Đã đánh dấu FAILED cho " + updatedCount + " thử thách quá hạn tính đến ngày " + today);
    }
}
