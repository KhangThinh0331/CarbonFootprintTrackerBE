package com.khangthinh.carbonfootprinttracker.repository;

import com.khangthinh.carbonfootprinttracker.entity.EmissionFactor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmissionFactorRepository extends JpaRepository<EmissionFactor, Long> {
    Page<EmissionFactor> findByCategoryId(Long categoryId, Pageable pageable);
}
