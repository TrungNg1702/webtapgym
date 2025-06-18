package com.project.WebTapGym.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "workout_schedule_status")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkoutScheduleStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private WorkoutSchedule schedule;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Boolean completed;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
