package com.project.WebTapGym.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MuscleGroupDTO {
    @NotNull(message = "khong duoc de trong")
    @JsonProperty("group_name")
    private String groupName;

    @JsonProperty("main_group_id")
    private Long mainGroupId;
}
