package com.khangthinh.carbonfootprinttracker.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OcrResponse {
    private String rawText;
    private Double extractedValue;
    private String category;
}