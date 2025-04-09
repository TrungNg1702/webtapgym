package com.project.WebTapGym.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "muscle_main_groups")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MuscleMainGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "muscleMainGroup", cascade = CascadeType.ALL, orphanRemoval = true
    )
    @JsonManagedReference

    private List<MuscleGroup> muscleGroups;
}
