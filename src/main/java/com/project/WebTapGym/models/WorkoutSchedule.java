package com.project.WebTapGym.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "workout_schedule")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkoutSchedule{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "day_of_week", nullable = false)
    private String dayOfWeek;

    @Column(name = "time_slot", nullable = false)
    private String timeSlot;

    @Column(name = "workout_type", nullable = false)
    private String workoutType;

    @Column(name = "duration", nullable = false)
    private Integer duration;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    @ManyToMany
    @JoinTable(
            name = "workout_schedule_exercise",
            joinColumns = @JoinColumn(name = "workout_schedule_id"),
            inverseJoinColumns = @JoinColumn(name = "exercise_id")
    )
    @JsonIgnore
    private List<Exercise> exercises;

}
