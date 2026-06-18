package com.khangthinh.carbonfootprinttracker.controller;

import com.khangthinh.carbonfootprinttracker.dto.QuizResultResponse;
import com.khangthinh.carbonfootprinttracker.dto.QuizSubmitRequest;
import com.khangthinh.carbonfootprinttracker.entity.Challenge;
import com.khangthinh.carbonfootprinttracker.service.ChallengeService;
import com.khangthinh.carbonfootprinttracker.service.UserChallengeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;
    private final UserChallengeService userChallengeService;

    @GetMapping
    public ResponseEntity<?> getAllChallenges(@PageableDefault(size = 20, page = 0) Pageable pageable) {
        return ResponseEntity.ok(challengeService.getAllChallenges(pageable));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createChallenge(@Valid @RequestBody Challenge challenge) {
            Challenge newChallenge = challengeService.createChallenge(challenge);
            return ResponseEntity.ok(newChallenge);
    }
}