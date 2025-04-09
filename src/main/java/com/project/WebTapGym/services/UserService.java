package com.project.WebTapGym.services;

import com.project.WebTapGym.components.JwtTokenUtil;
import com.project.WebTapGym.configurations.SecurityConfig;
import com.project.WebTapGym.dtos.UserDTO;
import com.project.WebTapGym.exceptions.DataNotFoundException;
import com.project.WebTapGym.models.Role;
import com.project.WebTapGym.models.User;
import com.project.WebTapGym.repositories.RoleRepository;
import com.project.WebTapGym.repositories.UserRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
//    private final SecurityConfig securityConfig;
    private final AuthenticationManager authenticationManager;
    @Override
    public User createUser(UserDTO userDTO) {
        String phone = userDTO.getPhone();
        if(userRepository.existsByPhone(phone)) {
            throw new ValidationException("Phone already exists");
        }
        User newUser = User.builder()
                .username(userDTO.getUserName())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .fullName(userDTO.getFullName())
                .phone(userDTO.getPhone())
                .address(userDTO.getAddress())
                .dateOfBirth(userDTO.getDateOfBirth())
                .heightCm(userDTO.getHeight())
                .weightKg(userDTO.getWeight())
//                .subscriptionMonths(userDTO.getSubscriptionMonths())
//                .registrationDate(LocalDateTime.now())
//                .expirationDate(LocalDateTime.now().plusMonths(userDTO.getSubscriptionMonths()))
                .build();

        Role role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new ValidationException("Role not found"));

        newUser.setRole(role);
        String password = userDTO.getPassword();
        String encodePassword = passwordEncoder.encode(password);
        newUser.setPassword(encodePassword);
        
        return userRepository.save(newUser);
    }

    @Override
    public String login(String phone, String password) throws DataNotFoundException {
        Optional<User> optionalUser = userRepository.findByPhone(phone);
        if(optionalUser.isEmpty()){
            throw new DataNotFoundException("User not found");
        }
//        return optionalUser.get();
        User existingUser = optionalUser.get();
        // check pass
        if(!passwordEncoder.matches(password, existingUser.getPassword()))
        {
            throw new DataNotFoundException("Wrong phone or password");
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                phone, password, existingUser.getAuthorities()
        );
        // authenticate with JV Spring Security
        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtil.generateToken(existingUser);
    }
}
