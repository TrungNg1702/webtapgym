package com.project.WebTapGym.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.WebTapGym.models.Exercise;
import com.project.WebTapGym.models.ExerciseVideo;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseResponse extends BaseResponse{

    @JsonProperty("exercise_id")
    private Long id;

    @JsonProperty("exercise_name")
    private String exerciseName;

    @JsonProperty("muscle_group_id")
    private Long muscleGroupId;

    @JsonProperty("muscle_section")
    private String muscleSection;

    @JsonProperty("technique_description")
    private String techniqueDescription;

    @JsonProperty("equipment_required")
    private String equipmentRequired;

    @JsonProperty("target_muscle_percentage")
    private String targetMusclePercentage;

    @JsonProperty("recommended_sets")
    private Long recommendedSets;

    @JsonProperty("recommended_reps")
    private Long recommendedReps;

    @JsonProperty("rest_between_sets")
    private Long restBetweenSets;

    // Thay đổi từ List<String> sang List<ExerciseVideoResponse>
    @JsonProperty("videos") // Đổi tên thuộc tính từ video_urls sang videos
    private List<ExerciseVideoResponse> videos;

    public static ExerciseResponse fromExercise (Exercise exercise){
        ExerciseResponse exerciseResponse = ExerciseResponse.builder()
                .id(exercise.getId())
                .exerciseName(exercise.getName())
                // Đảm bảo muscleGroup không null trước khi gọi getId()
                .muscleGroupId(exercise.getMuscleGroup() != null ? exercise.getMuscleGroup().getId() : null)
                .muscleSection(exercise.getMuscleSection())
                .techniqueDescription(exercise.getTechniqueDescription())
                .equipmentRequired(exercise.getEquipmentRequired())
                .targetMusclePercentage(exercise.getTargetMusclePercentage())
                .recommendedSets(exercise.getRecommendedSets())
                .recommendedReps(exercise.getRecommendedReps())
                .restBetweenSets(exercise.getRestBetweenSets())
                // Ánh xạ từ ExerciseVideo sang ExerciseVideoResponse
                .videos(exercise.getExerciseVideos().stream()
                        .map(ExerciseVideoResponse::fromExerciseVideo)
                        .collect(Collectors.toList()))
                .build();
        exerciseResponse.setCreatedAt(exercise.getCreatedAt());
        exerciseResponse.setUpdatedAt(exercise.getUpdatedAt());
        return exerciseResponse;
    }
}