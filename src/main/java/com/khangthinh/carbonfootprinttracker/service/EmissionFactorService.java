package com.khangthinh.carbonfootprinttracker.service;

import com.khangthinh.carbonfootprinttracker.entity.EmissionFactor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmissionFactorService {
    Page<EmissionFactor> getFactorsByCategoryId(Long categoryId, Pageable pageable);
}
