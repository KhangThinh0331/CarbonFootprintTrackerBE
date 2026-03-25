package com.khangthinh.carbonfootprinttracker.dto;

import lombok.Data;

@Data
public class UserProfileResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String avatarUrl;
    private Double targetCo2Month;
    private Integer totalPoints;
}
