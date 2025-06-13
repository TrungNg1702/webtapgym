package com.project.WebTapGym.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.WebTapGym.TestSecurityConfig;
import com.project.WebTapGym.dtos.UserDTO;
import com.project.WebTapGym.dtos.UserLoginDTO;
import com.project.WebTapGym.exceptions.DataNotFoundException;
import com.project.WebTapGym.models.Role;
import com.project.WebTapGym.models.User;
import com.project.WebTapGym.responses.LoginResponse;
import com.project.WebTapGym.services.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({TestSecurityConfig.class, UserControllerTest.MockConfig.class})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IUserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public IUserService userService() {
            return Mockito.mock(IUserService.class);
        }
    }

    @BeforeEach
    void setup() {
        reset(userService);
    }

    @Test
    void testCreateUser() throws Exception {
        //  Tạo UserDTO
        UserDTO dto = new UserDTO();
        dto.setPhone("0909090909");
        dto.setPassword("123456");
        dto.setRetypePassword("123456"); // tránh lỗi "Password không khớp!"
        dto.setRoleId(2L);
        dto.setUserName("testuser");
        dto.setEmail("test@example.com");
        dto.setFullName("Test User");


        Role role = new Role();
        role.setId(2L);
        role.setRoleName("USER");

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRole(role);
        Mockito.when(userService.createUser(any())).thenReturn(user);


        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void testLoginSuccess() throws Exception {
        UserLoginDTO loginDTO = new UserLoginDTO();
        loginDTO.setPhone("0909090909");
        loginDTO.setPassword("123456");

        LoginResponse response = new LoginResponse(1L, "mocked-jwt-token", "USER");

        Mockito.when(userService.loginAndGetResponse(
                        loginDTO.getPhone(), loginDTO.getPassword()))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"))
                .andExpect(jsonPath("$.role_id").value("USER"));
    }

    @Test
    void testLoginUnauthorized() throws Exception {
        UserLoginDTO loginDTO = new UserLoginDTO();
        loginDTO.setPhone("0909090909");
        loginDTO.setPassword("wrongpass");

        Mockito.when(userService.loginAndGetResponse(
                        loginDTO.getPhone(), loginDTO.getPassword()))
                .thenThrow(new DataNotFoundException("Sai tài khoản hoặc mật khẩu"));

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Sai tài khoản hoặc mật khẩu"));
    }

}
