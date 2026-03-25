package com.khangthinh.carbonfootprinttracker.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaderboardResponse {
    private int rank;
    private String fullName;
    private String avatarUrl;
    private Double totalCo2;
    private String badge;
}
