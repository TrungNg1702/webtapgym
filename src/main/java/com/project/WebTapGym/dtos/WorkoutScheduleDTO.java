package com.project.WebTapGym.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutScheduleDTO {

    @JsonProperty("id")
    @Min(value = 1, message = "WorkoutSchedule's Id phai lon hon 0")
    private Long id;

    @JsonProperty("day_of_week")
    private String dayOfWeek;

    @JsonProperty("exercise_id")
    @Builder.Default
    private List<Long> exerciseIds = new ArrayList<>();


    @JsonProperty("time_slot")
    private String timeSlot;

    @JsonProperty("workout_type")
    private String workoutType;

    private Integer duration;

    @JsonProperty("user_id")
    private Long userId;

}
