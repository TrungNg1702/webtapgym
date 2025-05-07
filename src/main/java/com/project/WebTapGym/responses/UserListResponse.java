package com.project.WebTapGym.responses;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
public class UserListResponse {
    private List<UserResponse> users;
    private int totalPages;
}
