package com.project.WebTapGym.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "exercises")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Exercise extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exercise_id")
    private Long id;

    @Column(name = "exercise_name", unique = true, length = 255, nullable = false)
    private String name;

    @Column(name = "muscle_section", length = 100)
    private String muscleSection;

    @Column(name = "technique_description", columnDefinition = "TEXT") // Có thể dùng TEXT cho các mô tả dài
    private String techniqueDescription;

    @Column(name = "equipment_required", length = 255)
    private String equipmentRequired;

    @Column(name = "target_muscle_percentage")
    private String targetMusclePercentage;

    @Column(name = "recommended_sets")
    private Long recommendedSets;

    @Column(name = "recommended_reps")
    private Long recommendedReps;

    @Column(name = "rest_between_sets")
    private Long restBetweenSets;

    @ManyToOne
    @JoinColumn(name = "muscle_group_id")
    private MuscleGroup muscleGroup;

    @ManyToOne
    @JoinColumn(name = "muscle_main_groups_id")
    private MuscleMainGroup muscleMainGroup;

    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExerciseVideo> exerciseVideos;
}