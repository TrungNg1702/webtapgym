package com.project.WebTapGym.responses;

import com.project.WebTapGym.models.Exercise;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
public class WorkoutScheduleResponse {
    private Long id;
    private String dayOfWeek;
    private String timeSlot;
    private String workoutType;
    private Integer duration;
    private List<Exercise> exercises;

    public static WorkoutScheduleResponse fromWorkoutSchedule(com.project.WebTapGym.models.WorkoutSchedule workoutSchedule) {
        WorkoutScheduleResponse workoutScheduleResponse =WorkoutScheduleResponse.builder()
                .id(workoutSchedule.getId())
                .dayOfWeek(workoutSchedule.getDayOfWeek())
                .timeSlot(workoutSchedule.getTimeSlot())
                .workoutType(workoutSchedule.getWorkoutType())
                .duration(workoutSchedule.getDuration())
                .exercises(workoutSchedule.getExercises())
                .build();

        return workoutScheduleResponse;
    }
}
