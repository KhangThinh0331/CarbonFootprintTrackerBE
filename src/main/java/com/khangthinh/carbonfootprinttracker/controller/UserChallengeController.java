package com.khangthinh.carbonfootprinttracker.controller;

import com.khangthinh.carbonfootprinttracker.dto.QuizResultResponse;
import com.khangthinh.carbonfootprinttracker.dto.QuizSubmitRequest;
import com.khangthinh.carbonfootprinttracker.dto.UserProfileResponse;
import com.khangthinh.carbonfootprinttracker.service.UserChallengeService;
import com.khangthinh.carbonfootprinttracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/user-challenges")
@RequiredArgsConstructor
public class UserChallengeController {
    private final UserChallengeService userChallengeService;

    @PostMapping("/join")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> joinChallenge(Principal principal, @Valid @RequestBody QuizSubmitRequest request) {
        String username = principal.getName();
        QuizResultResponse response = userChallengeService.submitQuizAttempt(username, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/result")
    public ResponseEntity<?> getChallengeResult(@PathVariable("id") Long challengeId, Principal principal) {
        String username = principal.getName();
        QuizResultResponse response = userChallengeService.getQuizResult(username, challengeId);
        return ResponseEntity.ok(response);
    }
}