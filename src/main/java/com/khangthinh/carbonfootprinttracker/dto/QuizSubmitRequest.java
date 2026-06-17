package com.khangthinh.carbonfootprinttracker.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class QuizSubmitRequest {
    @NotNull(message = "Vui lòng chọn thử thách")
    private Long challengeId;

    @NotEmpty(message = "Danh sách câu trả lời không được để trống")
    private List<AnswerItem> answers;

    @Data
    public static class AnswerItem {
        @NotNull(message = "Vui lòng chọn câu hỏi")
        private Long questionId;

        @NotNull(message = "Vui lòng chọn đáp án")
        private Long selectedAnswerId;
    }
}