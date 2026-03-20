package com.khangthinh.carbonfootprinttracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank(message = "Vui lòng nhập mật khẩu cũ")
    private String oldPassword;

    @NotBlank(message = "Vui lòng nhập mật khẩu mới")
    @Size(min = 6, max = 255, message = "Mật khẩu mới phải có ít nhất 6 ký tự và không được vượt quá 255 ký tự")
    private String newPassword;
}