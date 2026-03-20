package com.khangthinh.carbonfootprinttracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserChallengeId implements Serializable {
    private Long user;
    private Long challenge;
}
