package com.khangthinh.carbonfootprinttracker.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class QuizResultResponse {
    private boolean isPassed;
    private int score;
    private int totalQuestions;
    private Integer pointsEarned;
    private List<QuestionResult> details;

    @Data
    @Builder
    public static class QuestionResult {
        private Long questionId;
        private Long selectedAnswerId;
        private Long correctAnswerId;
        private boolean isCorrect;
    }
}