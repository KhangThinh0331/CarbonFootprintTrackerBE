package com.khangthinh.carbonfootprinttracker.service.impl;

import com.khangthinh.carbonfootprinttracker.entity.EmissionFactor;
import com.khangthinh.carbonfootprinttracker.repository.CategoryRepository;
import com.khangthinh.carbonfootprinttracker.repository.EmissionFactorRepository;
import com.khangthinh.carbonfootprinttracker.service.EmissionFactorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmissionFactorServiceImpl implements EmissionFactorService {
    private final EmissionFactorRepository emissionFactorRepository;
    private final CategoryRepository categoryRepository;

    // Ví dụ: Lấy tất cả các loại "Phương tiện di chuyển" (CategoryId = 1)
    @Override
    public Page<EmissionFactor> getFactorsByCategoryId(Long categoryId, Pageable pageable) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new RuntimeException("Không tìm thấy danh mục này");
        }
        return emissionFactorRepository.findByCategoryId(categoryId, pageable);
    }
}
