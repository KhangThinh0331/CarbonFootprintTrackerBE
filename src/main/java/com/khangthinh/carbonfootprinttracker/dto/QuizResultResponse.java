package com.khangthinh.carbonfootprinttracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class QuizResultResponse {
    @JsonProperty("isPassed")
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
        @JsonProperty("isCorrect")
        private boolean isCorrect;
    }
}