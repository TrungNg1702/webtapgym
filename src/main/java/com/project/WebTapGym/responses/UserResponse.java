package com.project.WebTapGym.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.WebTapGym.models.User;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@AllArgsConstructor
@Builder
@Getter
@Setter
@NoArgsConstructor
public class UserResponse extends BaseResponse {
    @JsonProperty("username")
    private String userName;

    private String email;

    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("sex")
    private String sex;

    private String phone;

    private String address;

    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

    @JsonProperty("height_cm")
    private Float height;

    @JsonProperty("weight_kg")
    private Float weight;

    public static UserResponse from(User user) {
        UserResponse userResponse = UserResponse.builder()
                .userName(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .address(user.getAddress())
                .dateOfBirth(user.getDateOfBirth())
                .height(user.getHeightCm())
                .weight(user.getWeightKg())
                .sex(String.valueOf(user.getSex()))

                .build();

        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setUpdatedAt(user.getUpdatedAt());

        return userResponse;
    }
}
