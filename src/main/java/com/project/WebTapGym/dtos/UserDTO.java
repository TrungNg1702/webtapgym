package com.project.WebTapGym.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDTO {
    @JsonProperty("username")
    private String userName;

    private String email;

    @JsonProperty("password_hash")
    @NotNull(message = "Mật khẩu bạn muốn là gì? ")
    private String password;

    @JsonProperty("retype_password")
    private String retypePassword;

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

    @JsonProperty("subscription_months")
    private Integer subscriptionMonths;

    @JsonProperty("role_id")
    @NotNull(message = "Role not blank")
    private Long roleId;



}
