package com.khangthinh.carbonfootprinttracker.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ActivityLogRequest {
    @NotNull(message = "Vui lòng chọn hệ số phát thải (Hoạt động)")
    private Long factorId;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng không được là số âm")
    private Double quantity;

    @Size(max = 255, message = "Ghi chú không được vượt quá 255 ký tự")
    private String note;
}