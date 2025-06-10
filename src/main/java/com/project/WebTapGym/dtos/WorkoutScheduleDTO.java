package com.project.WebTapGym.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutScheduleDTO {
    @JsonProperty("day_of_week")
    private String dayOfWeek;
    @JsonProperty("time_slot")
    private String timeSlot;
    @JsonProperty("workout_type")
    private String workoutType;

    private Integer duration;

    @JsonProperty("user_id")
    private Long userId;

}
