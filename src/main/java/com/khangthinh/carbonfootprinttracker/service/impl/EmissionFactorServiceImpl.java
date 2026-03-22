package com.khangthinh.carbonfootprinttracker.service.impl;

import com.khangthinh.carbonfootprinttracker.dto.EmissionFactorResponse;
import com.khangthinh.carbonfootprinttracker.entity.EmissionFactor;
import com.khangthinh.carbonfootprinttracker.mapper.EmissionFactorMapper;
import com.khangthinh.carbonfootprinttracker.repository.CategoryRepository;
import com.khangthinh.carbonfootprinttracker.repository.EmissionFactorRepository;
import com.khangthinh.carbonfootprinttracker.service.EmissionFactorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmissionFactorServiceImpl implements EmissionFactorService {
    private final EmissionFactorRepository emissionFactorRepository;
    private final CategoryRepository categoryRepository;

    private final EmissionFactorMapper emissionFactorMapper;

    // Ví dụ: Lấy tất cả các loại "Phương tiện di chuyển" (CategoryId = 1)
    @Override
    public List<EmissionFactorResponse> getFactorsByCategoryId(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new RuntimeException("Không tìm thấy danh mục này");
        }
        List<EmissionFactor> emissionFactors = emissionFactorRepository.findByCategoryId(categoryId);
        return emissionFactors.stream()
                .map(emissionFactorMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
