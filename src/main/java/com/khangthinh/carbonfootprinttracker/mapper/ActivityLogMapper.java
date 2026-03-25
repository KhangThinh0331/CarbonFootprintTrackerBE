package com.khangthinh.carbonfootprinttracker.mapper;

import com.khangthinh.carbonfootprinttracker.dto.ActivityLogResponse;
import com.khangthinh.carbonfootprinttracker.entity.ActivityLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ActivityLogMapper {

    // Nếu tên thuộc tính của Entity và DTO giống nhau (vd: id, quantity), MapStruct tự map.
    // Nếu khác nhau hoặc nằm ở class con, bạn dùng @Mapping để chỉ định.
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "emissionFactor.category.name", target = "categoryName")
    @Mapping(source = "emissionFactor.activityName", target = "activityName")
    @Mapping(source = "emissionFactor.unit", target = "unit")
    ActivityLogResponse toResponseDto(ActivityLog activityLog);

}
