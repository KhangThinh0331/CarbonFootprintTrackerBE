package com.khangthinh.carbonfootprinttracker.service;

import com.khangthinh.carbonfootprinttracker.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    Page<Category> getAllCategories(Pageable pageable);
}
