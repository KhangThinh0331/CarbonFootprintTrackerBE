package com.khangthinh.carbonfootprinttracker.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "otp_tokens", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "type"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "otp_code", nullable = false, length = 6, unique = true)
    private String otpCode;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate; // Thời gian hết hạn

    @CreatedDate
    @Column(name = "last_sent_at", nullable = false)
    private LocalDateTime lastSentAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private OtpType type;

    // Liên kết 1-1 với User (Mỗi token thuộc về 1 user)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    public enum OtpType {
        REGISTRATION,
        FORGOT_PASSWORD
    }
}