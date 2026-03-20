package com.khangthinh.carbonfootprinttracker.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String username;
    private String message;
}
