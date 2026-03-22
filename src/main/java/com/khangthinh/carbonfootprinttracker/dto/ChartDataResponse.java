package com.khangthinh.carbonfootprinttracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChartDataResponse {
    private String name; // Sẽ chứa ngày (VD: "12/03" hoặc "T2")
    private Double co2;  // Tổng CO2 trong ngày
}
