package com.project.WebTapGym.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    @NotNull(message = "khong duoc de trong")
    private String name;
}
