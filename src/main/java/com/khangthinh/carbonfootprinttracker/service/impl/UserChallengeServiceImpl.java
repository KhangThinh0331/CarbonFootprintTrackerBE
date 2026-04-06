package com.khangthinh.carbonfootprinttracker.service.impl;

import com.khangthinh.carbonfootprinttracker.dto.UserChallengeId;
import com.khangthinh.carbonfootprinttracker.entity.Challenge;
import com.khangthinh.carbonfootprinttracker.entity.User;
import com.khangthinh.carbonfootprinttracker.entity.UserChallenge;
import com.khangthinh.carbonfootprinttracker.repository.ChallengeRepository;
import com.khangthinh.carbonfootprinttracker.repository.UserChallengeRepository;
import com.khangthinh.carbonfootprinttracker.repository.UserRepository;
import com.khangthinh.carbonfootprinttracker.service.UserChallengeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserChallengeServiceImpl implements UserChallengeService {
    private final UserChallengeRepository userChallengeRepository;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;

    // 1. Tham gia thử thách
    @Transactional
    @Override
    public UserChallenge joinChallenge(String username, Long challengeId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Thử thách không tồn tại"));

        // Kiểm tra xem đã tham gia chưa
        if (userChallengeRepository.existsByUserAndChallenge(user, challenge)) {
            throw new RuntimeException("Bạn đã tham gia thử thách này rồi!");
        }

        UserChallenge userChallenge = UserChallenge.builder()
                .user(user)
                .challenge(challenge)
                .status(UserChallenge.ChallengeStatus.IN_PROGRESS)
                .build();

        return userChallengeRepository.save(userChallenge);
    }

    // 2. Xác nhận hoàn thành thử thách
    @Transactional
    @Override
    public void completeChallenge(Long userId, Long challengeId) {
        UserChallengeId id = new UserChallengeId(userId, challengeId);
        UserChallenge uc = userChallengeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dữ liệu tham gia"));

        if (uc.getStatus() == UserChallenge.ChallengeStatus.COMPLETED) {
            throw new RuntimeException("Thử thách này đã được hoàn thành trước đó!");
        }

        uc.setStatus(UserChallenge.ChallengeStatus.COMPLETED);
        userChallengeRepository.save(uc);

        // Nâng cao: Cộng điểm thưởng (points) vào bảng User ở đây
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Thử thách"));

        int currentPoints = (user.getTotalPoints() != null) ? user.getTotalPoints() : 0;
        user.setTotalPoints(currentPoints + challenge.getPoints());

        userRepository.save(user);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    @Override
    public void markFailedChallenges() {
        LocalDate today = LocalDate.now();

        // Gọi repository xử lý
        int updatedCount = userChallengeRepository.updateFailedChallenges(today);

        System.out.println("Đã đánh dấu FAILED cho " + updatedCount + " thử thách quá hạn tính đến ngày " + today);
    }
}
