package com.khangthinh.carbonfootprinttracker.controller;

import com.khangthinh.carbonfootprinttracker.service.EmissionFactorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/emission-factors")
@RequiredArgsConstructor
public class EmissionFactorController {

    private final EmissionFactorService emissionFactorService;

    // Lấy danh sách hệ số phát thải theo ID của danh mục (Ví dụ: /api/emission-factors/category/1)
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getFactorsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(emissionFactorService.getFactorsByCategoryId(categoryId));
    }
}