package com.project.WebTapGym.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "musclegroups")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MuscleGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "muscle_group_id")
    private Long id;

    @Column(name = "group_name",length = 100)
    private String groupName;

    @ManyToOne
    @JoinColumn(name = "main_group_id")
    @JsonIgnore
    private MuscleMainGroup muscleMainGroup;
}
