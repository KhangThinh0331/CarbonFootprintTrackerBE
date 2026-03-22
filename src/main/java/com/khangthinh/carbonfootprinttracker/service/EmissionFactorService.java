package com.khangthinh.carbonfootprinttracker.service;

import com.khangthinh.carbonfootprinttracker.dto.EmissionFactorResponse;

import java.util.List;

public interface EmissionFactorService {
    List<EmissionFactorResponse> getFactorsByCategoryId(Long categoryId);
}
