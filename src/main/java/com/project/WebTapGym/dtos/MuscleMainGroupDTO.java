package com.project.WebTapGym.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MuscleMainGroupDTO {
    @NotNull(message = "không được để trống")
    private String name;


}
