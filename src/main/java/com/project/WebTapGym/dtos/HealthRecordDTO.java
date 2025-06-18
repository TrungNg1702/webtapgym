package com.project.WebTapGym.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HealthRecordDTO {
    private Double weight;
    private Double height;
    @JsonProperty("body_fat")
    private Double bodyFat;
    @JsonProperty("calories_in")
    private Integer caloriesIn;
    @JsonProperty("calories_out")
    private Integer caloriesOut;
    private String note;
}
