package com.project.WebTapGym.services;

import com.project.WebTapGym.dtos.UserDTO;
import com.project.WebTapGym.exceptions.DataNotFoundException;
import com.project.WebTapGym.models.User;


public interface IUserService {
    User createUser(UserDTO userDTO);

    String login(String phone, String password) throws DataNotFoundException;
}
