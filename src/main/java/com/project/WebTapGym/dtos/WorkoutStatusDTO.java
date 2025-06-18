package com.project.WebTapGym.dtos;


import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WorkoutStatusDTO {
    private Long scheduleId;
    private Boolean completed;
}
