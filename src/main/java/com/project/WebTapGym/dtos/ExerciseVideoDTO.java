package com.project.WebTapGym.dtos;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseVideoDTO {

    @JsonProperty("exercise_id")
    @Min(value = 1, message = "Exercise ID must be > 0")
    private Long exerciseId;

    @JsonProperty("video_url")
    @Size(min = 1, max = 300, message = "Video URL is invalid")
    private String videoUrl;
}
