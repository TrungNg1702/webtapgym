package com.project.WebTapGym.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.WebTapGym.exceptions.DataNotFoundException;
import com.project.WebTapGym.models.User;
import com.project.WebTapGym.repositories.UserRepository;
import com.project.WebTapGym.responses.LoginResponse;
import com.project.WebTapGym.responses.UserListResponse;
import com.project.WebTapGym.responses.UserResponse;
import com.project.WebTapGym.services.IUserService;
import com.project.WebTapGym.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.project.WebTapGym.dtos.*;


import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("${api.prefix}/users")
//@Validated
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;


    @PostMapping("/register")
    public ResponseEntity<?> createUser(
            @Valid @RequestBody UserDTO userDTO,
            BindingResult result) {
       try{
           if (result.hasErrors()) {
               List<String> errorMessages = result.getFieldErrors()
                       .stream()
                       .map(error -> error.getField() + ": " + error.getDefaultMessage())
                       .toList();
               return ResponseEntity.badRequest().body(errorMessages);
           }
           if (!userDTO.getPassword().equals(userDTO.getRetypePassword())){
               return ResponseEntity.badRequest().body("Password không khớp!");
           }
           User user = userService.createUser(userDTO);
           return ResponseEntity.ok(user);
       } catch (Exception e){
           return ResponseEntity.badRequest().body(e.getMessage());
       }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        try {
            LoginResponse response = userService.loginAndGetResponse(
                    userLoginDTO.getPhone(), userLoginDTO.getPassword()
            );
            return ResponseEntity.ok(response);
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi đăng nhập: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/unlock")
    public ResponseEntity<?> unlockUser(
            @PathVariable("id") Long userID
    ){
        try{
            User unlockUser = userService.unlockUser(userID);
            return ResponseEntity.ok("Đã mở khóa thành công User có ID là " + userID);
        } catch (DataNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable("id") Long userID,
            @Valid @RequestBody UserUpdateDTO userUpdateDTO
    ){
        try{
            User updateUser = userService.updateUser(userID, userUpdateDTO);
            return ResponseEntity.ok(UserResponse.from(updateUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") Long userId) {
        try {
            User user = userService.getUserById(userId);
            return ResponseEntity.ok(UserResponse.from(user));
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi lấy thông tin người dùng: " + e.getMessage());
        }
    }

    @GetMapping("")
    public ResponseEntity<UserListResponse> getUsers(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ){
        PageRequest pageRequest = PageRequest.of(page, limit,
                Sort.by("createdAt").descending());

        Page<UserResponse> userPage = userService.getAllUser(pageRequest);

        int totalPages = userPage.getTotalPages();
        List<UserResponse> users = userPage.getContent();

        return ResponseEntity.ok(UserListResponse.builder()
                        .users(users)
                        .totalPages(totalPages)
                .build());
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId, @RequestBody BanRequest banRequest) {
        try {
            String banReason = banRequest != null && banRequest.getBanReason() != null
                    ? banRequest.getBanReason()
                    : null;
            userService.deleteUser(userId, banReason);
            return ResponseEntity.ok("User banned successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
class BanRequest {
    @JsonProperty("ban_reason")
    private String banReason;

    public String getBanReason() {
        return banReason;
    }

    public void setBanReason(String banReason) {
        this.banReason = banReason;
    }
}