package com.project.WebTapGym.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.WebTapGym.models.Exercise;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


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

    @JsonProperty("video_url")
    private String videoUrl;


    public static ExerciseResponse fromExercise (Exercise exercise){
        ExerciseResponse exerciseResponse = ExerciseResponse.builder()
                .id(exercise.getId())
                .exerciseName(exercise.getName())
                .muscleGroupId(exercise.getMuscleGroup().getId())
                .muscleSection(exercise.getMuscleSection())
                .techniqueDescription(exercise.getTechniqueDescription())
                .equipmentRequired(exercise.getEquipmentRequired())
                .targetMusclePercentage(exercise.getTargetMusclePercentage())
                .recommendedSets(exercise.getRecommendedSets())
                .recommendedReps(exercise.getRecommendedReps())
                .restBetweenSets(exercise.getRestBetweenSets())
                .videoUrl(exercise.getVideoUrl())
                .build();
        exerciseResponse.setCreatedAt(exercise.getCreatedAt());
        exerciseResponse.setUpdatedAt(exercise.getUpdatedAt());
        return exerciseResponse;
    }
}
