package com.khangthinh.carbonfootprinttracker.dto;

import com.khangthinh.carbonfootprinttracker.entity.Goal;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class GoalResponse {

    private Long id;
    private String goalName;
    private Double targetValue;
    private Double currentValue;
    private LocalDate deadline;
    private Goal.GoalStatus status;
}
