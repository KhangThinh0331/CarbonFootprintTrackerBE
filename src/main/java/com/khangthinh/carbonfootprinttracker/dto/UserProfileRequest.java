package com.khangthinh.carbonfootprinttracker.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserProfileRequest {
    @NotBlank(message = "Tên người dùng không được để trống")
    @Size(max = 100, message = "Tên người dùng không được vượt quá 100 ký tự")
    private String fullName;

    private String avatarUrl;

    @NotNull(message = "Khối lượng khí carbonic không được để trống")
    @Min(value = 0, message = "Khối lượng khí carbonic không được là số âm")
    private Double targetCo2Month;
}
