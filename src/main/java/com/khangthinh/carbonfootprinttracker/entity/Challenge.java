package com.khangthinh.carbonfootprinttracker.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "challenges")
public class Challenge {
    @Id
    @NotBlank(message = "ID không được để trống")
    @Positive(message = "ID phải lớn hơn 0")
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 100, message = "Ghi chú không được vượt quá 100 ký tự")
    @Column(name = "title")
    private String title;

    @NotBlank(message = "Mô tả không được để trống.")
    @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
    @Column(name = "description")
    private String description;

    @NotBlank(message = "Điểm số không được để trống.")
    @Min(value = 0, message = "Điểm số không được là số âm")
    @Column(name = "points")
    private Integer points;

    @NotBlank(message = "Vui lòng chọn ngày bắt đầu")
    @Column(name = "start_date")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate startDate;

    @NotBlank(message = "Vui lòng chọn ngày kết thúc")
    @Column(name = "end_date")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate endDate;
}
