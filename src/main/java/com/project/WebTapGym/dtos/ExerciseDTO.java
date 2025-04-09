package com.project.WebTapGym.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseDTO {

    @NotBlank(message = "Name is required")
    @JsonProperty("exercise_name")
    private String exerciseName;

    @JsonProperty("muscle_group_id")
    private Long muscleGroupId;

    @JsonProperty("muscle_section")
    private String muscleSection;

    @NotBlank(message = "This part cannot be blank")
    @JsonProperty("technique_description")
    private String techniqueDescription;

    @JsonProperty("equipment_required")
    private String equipmentRequired;

    @JsonProperty("target_muscle_percentage")
    private String targetMusclePercentage;

    @Min(value = 1, message = "Số set tập ít nhất phải là 1 set")
    @Max(value = 4, message = "Số set lớn nhất là 4 sets")
    @JsonProperty("recommended_sets")
    private Long recommendedSets;

    @Min(value = 3, message = "Số rep thực hiện tối thiểu là 3 reps")
    @Max(value = 13, message = "Số rep lớn nhất nên thực hiện là 13 reps")
    @JsonProperty("recommended_reps")
    private Long recommendedReps;

    @JsonProperty("rest_between_sets")
    private Long restBetweenSets;
}
