package com.khangthinh.carbonfootprinttracker.mapper;

import com.khangthinh.carbonfootprinttracker.dto.UserProfileRequest;
import com.khangthinh.carbonfootprinttracker.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserProfileMapper {
    void updateUserFromRequest(UserProfileRequest request, @MappingTarget User user);
}
