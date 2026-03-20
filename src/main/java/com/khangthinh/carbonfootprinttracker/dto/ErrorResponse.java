package com.khangthinh.carbonfootprinttracker.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private LocalDateTime timestamp; // Thời gian xảy ra lỗi
    private int status;              // Mã lỗi HTTP (400, 401, 404, 500...)
    private String error;            // Tên lỗi (Bad Request, Not Found...)
    private String message;          // Lời nhắn chi tiết (vd: "Bạn đã tham gia thử thách này rồi!")
    private String path;             // API nào gây ra lỗi (vd: /api/user-challenges/1/join)

    private Map<String, String> validationErrors;
}