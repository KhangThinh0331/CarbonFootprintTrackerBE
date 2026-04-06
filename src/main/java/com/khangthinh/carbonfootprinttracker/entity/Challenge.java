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
    @NotNull(message = "ID không được để trống")
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

    @NotNull(message = "Điểm số không được để trống.")
    @Min(value = 0, message = "Điểm số không được là số âm")
    @Column(name = "points")
    private Integer points;

    @NotNull(message = "Vui lòng chọn ngày bắt đầu")
    @FutureOrPresent(message = "Ngày bắt đầu phải là hiện tại hoặc tương lai")
    @Column(name = "start_date")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate startDate;

    @NotNull(message = "Vui lòng chọn ngày kết thúc")
    @Future(message = "Ngày kết thúc phải ở tương lai")
    @Column(name = "end_date")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate endDate;

    @NotNull(message = "Mục tiêu CO2 không được để trống")
    @Positive(message = "Mục tiêu CO2 phải lớn hơn 0")
    @Column(name = "target_co2")
    private Double targetCo2;

    @AssertTrue(message = "Ngày kết thúc phải sau ngày bắt đầu")
    private boolean isValidDateRange() {
        if (startDate == null || endDate == null) {
            return true;
        }
        return startDate.isBefore(endDate) || startDate.isEqual(endDate);
    }
}
