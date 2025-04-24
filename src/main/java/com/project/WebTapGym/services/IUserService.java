package com.project.WebTapGym.services;

import com.project.WebTapGym.dtos.UserDTO;
import com.project.WebTapGym.dtos.UserUpdateDTO;
import com.project.WebTapGym.exceptions.DataNotFoundException;
import com.project.WebTapGym.models.User;
import com.project.WebTapGym.responses.LoginResponse;


public interface IUserService {
    User createUser(UserDTO userDTO);

    String login(String phone, String password) throws DataNotFoundException;

    User updateUser(Long userId, UserUpdateDTO userUpdateDTO) throws DataNotFoundException;

    User getUserById(Long userId) throws DataNotFoundException;

    LoginResponse loginAndGetResponse(String phone, String password) throws DataNotFoundException;
}
