package com.project.WebTapGym.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.WebTapGym.models.ExerciseVideo;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseVideoResponse {
    @JsonProperty("video_id")
    private Long id;

    @JsonProperty("video_url")
    private String videoUrl;

    public static ExerciseVideoResponse fromExerciseVideo(ExerciseVideo exerciseVideo) {
        return ExerciseVideoResponse.builder()
                .id(exerciseVideo.getId())
                .videoUrl(exerciseVideo.getVideoUrl())
                .build();
    }
}