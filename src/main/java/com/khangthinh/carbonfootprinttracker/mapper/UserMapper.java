package com.khangthinh.carbonfootprinttracker.mapper;

import com.khangthinh.carbonfootprinttracker.dto.UserProfileResponse;
import com.khangthinh.carbonfootprinttracker.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserProfileResponse toResponseDto(User user);
}
