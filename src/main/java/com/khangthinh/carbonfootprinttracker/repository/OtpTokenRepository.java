package com.khangthinh.carbonfootprinttracker.repository;

import com.khangthinh.carbonfootprinttracker.entity.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    // Tìm token theo mã OTP (dùng khi user nhập mã xác minh)
    Optional<OtpToken> findByOtpCodeAndType(String otpCode, OtpToken.OtpType type);
    Optional<OtpToken> findByUserIdAndType(Long userId, OtpToken.OtpType type);
    @Modifying
    @Query("DELETE FROM OtpToken o WHERE o.user.id = :userId AND o.type = :type")
    void deleteByUserIdAndType(Long userId, OtpToken.OtpType type);
}
