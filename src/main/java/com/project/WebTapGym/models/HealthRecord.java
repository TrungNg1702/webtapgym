package com.project.WebTapGym.models;


import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.checker.units.qual.C;

import java.time.LocalDate;

@Entity
@Table(name = "health_records")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HealthRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Double weight;

    private Double height;
    @Column(name = "body_fat")
    private Double bodyFat;

    @Column(name = "calories_in")
    private Integer caloriesIn;

    @Column(name = "calories_out")
    private Integer caloriesOut;

    private LocalDate date;

    @Column(columnDefinition = "TEXT")
    private String note;
}
