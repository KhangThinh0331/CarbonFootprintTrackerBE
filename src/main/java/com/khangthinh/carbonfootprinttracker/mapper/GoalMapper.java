package com.khangthinh.carbonfootprinttracker.mapper;

import com.khangthinh.carbonfootprinttracker.dto.GoalResponse;
import com.khangthinh.carbonfootprinttracker.entity.Goal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GoalMapper {
    @Mapping(source = "status", target = "status")
    GoalResponse toResponseDto(Goal goal);
}
