package com.khangthinh.carbonfootprinttracker.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ActivityLogResponse {
    private Long id;
    private String username;           // Lấy từ User entity
    private String categoryName;       // Lấy từ EmissionFactor -> Category
    private String activityName;       // Lấy từ EmissionFactor
    private String unit;
    private Double quantity;
    private Double totalCo2;
    private String note;
    private LocalDateTime loggedAt;
}
