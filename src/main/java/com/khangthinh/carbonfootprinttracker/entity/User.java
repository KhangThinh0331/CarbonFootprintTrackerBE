package com.khangthinh.carbonfootprinttracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "target_co2_month")
    private Double targetCo2Month;

    @CreatedDate
    @Column(name = "create_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "total_points")
    @Builder.Default
    private Integer totalPoints = 0;

    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = false;

    @ManyToMany(fetch = FetchType.EAGER) // EAGER để lấy luôn Role khi lấy User
    @JoinTable(
            name = "user_roles", // Tên bảng trung gian trong DB
            joinColumns = @JoinColumn(name = "user_id"), // Khóa ngoại trỏ tới User
            inverseJoinColumns = @JoinColumn(name = "role_id") // Khóa ngoại trỏ tới Role
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();
}
