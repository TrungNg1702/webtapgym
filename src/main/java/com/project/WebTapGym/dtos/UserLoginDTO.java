package com.project.WebTapGym.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDTO {

    @NotNull(message = "Không nhập thiếu số điện thoại")
    private String phone;

    @JsonProperty("password_hash")
    @NotNull(message = "Bạn phải nhập mật khẩu mới đăng nhập được nhé ^^ ")
    private String password;
}
