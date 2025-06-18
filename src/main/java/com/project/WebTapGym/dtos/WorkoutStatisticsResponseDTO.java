package com.project.WebTapGym.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkoutStatisticsResponseDTO {
    private long totalSessions;
    private long completedSessions;
    private double efficiency;
}
