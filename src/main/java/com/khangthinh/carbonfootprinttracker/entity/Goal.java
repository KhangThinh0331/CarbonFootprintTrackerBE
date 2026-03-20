package com.khangthinh.carbonfootprinttracker.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Table(name = "goals")
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Tên mục tiêu không được để trống")
    @Size(max = 100, message = "Tên mục tiêu không được vượt quá 100 ký tự")
    @Column(name = "goal_name")
    private String goalName;

    @NotBlank(message = "Điểm mục tiêu không được để trống")
    @Min(value = 0, message = "Điểm mục tiêu không được là số âm")
    @Column(name = "target_value")
    private Double targetValue;

    @Column(name = "current_value")
    private Double currentValue;

    @NotBlank(message = "Vui lòng chọn thời hạn mục tiêu")
    @Column(name = "deadline")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate deadline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
