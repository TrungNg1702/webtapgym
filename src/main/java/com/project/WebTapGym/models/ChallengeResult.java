package com.project.WebTapGym.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "challenge_results")
@Getter
@Setter
public class ChallengeResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @ManyToOne
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;


    @Column(name = "is_completed")
    private boolean isCompleted;


    @Column(name = "completed_at")
    private LocalDateTime completedAt;


    private Integer progress;


    private String note;

}
