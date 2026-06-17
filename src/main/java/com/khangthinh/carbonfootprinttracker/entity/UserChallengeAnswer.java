package com.khangthinh.carbonfootprinttracker.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_challenge_answers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserChallengeAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_answer_id")
    private Answer selectedAnswer;
}
