package com.khangthinh.carbonfootprinttracker.dto;

import lombok.Data;

@Data
public class EmissionFactorResponse {
    private Long id;
    private String activityName;
    private String unit;
    private Double co2PerUnit;
}
