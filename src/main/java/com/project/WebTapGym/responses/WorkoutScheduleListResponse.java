package com.project.WebTapGym.responses;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
public class WorkoutScheduleListResponse {
    private List<WorkoutScheduleResponse> workoutSchedules;
    private int totalPages;
}
