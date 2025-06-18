package com.project.WebTapGym.responses;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HealthRecordResponse {
    private LocalDate date;
    private Double weight;
    private Double bodyFat;
    private Integer caloriesIn;
    private Integer caloriesOut;
    private String note;

}
