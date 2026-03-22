package com.khangthinh.carbonfootprinttracker.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
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

    @NotNull(message = "Điểm mục tiêu không được để trống")
    @Min(value = 0, message = "Điểm mục tiêu không được là số âm")
    @Column(name = "target_value")
    private Double targetValue;

    @Column(name = "current_value")
    private Double currentValue;

    @NotNull(message = "Vui lòng chọn thời hạn mục tiêu")
    @Future(message = "Thời hạn phải ở tương lai")
    @Column(name = "deadline")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate deadline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
