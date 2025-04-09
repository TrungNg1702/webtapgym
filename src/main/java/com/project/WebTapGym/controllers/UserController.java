package com.project.WebTapGym.controllers;

import com.project.WebTapGym.exceptions.DataNotFoundException;
import com.project.WebTapGym.models.User;
import com.project.WebTapGym.repositories.UserRepository;
import com.project.WebTapGym.services.IUserService;
import com.project.WebTapGym.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
//           return ResponseEntity.ok().body("Đăng ký thành công!");
           return ResponseEntity.ok(user);
       } catch (Exception e){
           return ResponseEntity.badRequest().body(e.getMessage());
       }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDTO userLoginDTO){
        // kiem tra thong tin dang nhap
        try {
            String token = userService.login(userLoginDTO.getPhone(), userLoginDTO.getPassword());
            // tra ve token response
            return ResponseEntity.ok(token);
        } catch (DataNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
