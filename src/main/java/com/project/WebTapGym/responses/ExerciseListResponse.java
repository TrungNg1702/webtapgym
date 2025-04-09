package com.project.WebTapGym.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ExerciseListResponse {
    private List<ExerciseResponse> exercises;
    private int totalPages;
}
