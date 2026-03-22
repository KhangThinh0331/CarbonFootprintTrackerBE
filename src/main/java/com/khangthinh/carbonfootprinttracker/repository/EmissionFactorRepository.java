package com.khangthinh.carbonfootprinttracker.repository;

import com.khangthinh.carbonfootprinttracker.entity.EmissionFactor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmissionFactorRepository extends JpaRepository<EmissionFactor, Long> {
    List<EmissionFactor> findByCategoryId(Long categoryId);
}
