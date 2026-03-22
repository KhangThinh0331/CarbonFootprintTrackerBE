package com.khangthinh.carbonfootprinttracker.mapper;

import com.khangthinh.carbonfootprinttracker.dto.EmissionFactorResponse;
import com.khangthinh.carbonfootprinttracker.entity.EmissionFactor;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmissionFactorMapper {
    EmissionFactorResponse toResponseDto(EmissionFactor emissionFactor);
}
