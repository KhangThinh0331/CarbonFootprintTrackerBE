package com.khangthinh.carbonfootprinttracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChartDataResponse {
    private String name;
    private Double co2;
}
