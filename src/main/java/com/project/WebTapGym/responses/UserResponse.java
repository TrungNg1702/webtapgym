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
    private Long id;

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

    @JsonProperty("is_active")
    private Boolean isActive;

    public static UserResponse from(User user) {
        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .userName(user.getUsername2())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .address(user.getAddress())
                .dateOfBirth(user.getDateOfBirth())
                .height(user.getHeightCm())
                .weight(user.getWeightKg())
                .sex(String.valueOf(user.getSex()))
                .isActive(user.isActive())
                .build();

        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setUpdatedAt(user.getUpdatedAt());

        return userResponse;
    }
}
