package com.project.WebTapGym.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserUpdateDTO {
    @JsonProperty("username")
    private String userName;

    private String email;

    @JsonProperty("full_name")
    private String fullName;

    @NotNull(message = "Không nhập thiếu số điện thoại")
    private String phone;

    private String address;

    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

    @JsonProperty("height_cm")
    private Float height;

    @JsonProperty("weight_kg")
    private Float weight;

}
