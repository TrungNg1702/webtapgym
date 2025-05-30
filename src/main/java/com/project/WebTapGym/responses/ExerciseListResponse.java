package com.project.WebTapGym.responses;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
public class ExerciseListResponse {
    private List<ExerciseResponse> exercises;
    private int totalPages;
}
