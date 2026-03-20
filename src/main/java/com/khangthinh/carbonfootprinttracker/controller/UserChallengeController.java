package com.khangthinh.carbonfootprinttracker.controller;

import com.khangthinh.carbonfootprinttracker.entity.User;
import com.khangthinh.carbonfootprinttracker.service.UserChallengeService;
import com.khangthinh.carbonfootprinttracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/user-challenges")
@RequiredArgsConstructor
public class UserChallengeController {

    private final UserChallengeService userChallengeService;
    private final UserService userService;

    // Tham gia một thử thách
    @PostMapping("/{challengeId}/join")
    public ResponseEntity<?> joinChallenge(@PathVariable Long challengeId, Principal principal) {
            var userChallenge = userChallengeService.joinChallenge(principal.getName(), challengeId);
            return ResponseEntity.ok(userChallenge);
    }

    // Hoàn thành một thử thách và nhận điểm
    @PutMapping("/{challengeId}/complete")
    public ResponseEntity<?> completeChallenge(@PathVariable Long challengeId, Principal principal) {
            // Lấy User ID từ Username trong Token
            User user = userService.getUserProfile(principal.getName());

            // Gọi hàm cộng điểm trong Service
            userChallengeService.completeChallenge(user.getId(), challengeId);

            return ResponseEntity.ok("Chúc mừng! Bạn đã hoàn thành thử thách và được cộng điểm.");
    }
}