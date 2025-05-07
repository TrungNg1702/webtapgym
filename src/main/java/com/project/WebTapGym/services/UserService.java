package com.project.WebTapGym.services;

import com.project.WebTapGym.components.JwtTokenUtil;
import com.project.WebTapGym.dtos.UserDTO;
import com.project.WebTapGym.dtos.UserUpdateDTO;
import com.project.WebTapGym.exceptions.DataNotFoundException;
import com.project.WebTapGym.models.Role;
import com.project.WebTapGym.models.User;
import com.project.WebTapGym.repositories.ExerciseRepository;
import com.project.WebTapGym.repositories.RoleRepository;
import com.project.WebTapGym.repositories.UserRepository;
import com.project.WebTapGym.responses.ExerciseResponse;
import com.project.WebTapGym.responses.LoginResponse;
import com.project.WebTapGym.responses.UserResponse;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private ExerciseRepository exerciseRepository;
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
                .sex(userDTO.getSex())

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

    public LoginResponse loginAndGetResponse(String phone, String password) throws DataNotFoundException {
        Optional<User> optionalUser = userRepository.findByPhone(phone);
        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException("User not found");
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new DataNotFoundException("Wrong phone or password");
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(phone, password, user.getAuthorities());
        authenticationManager.authenticate(authenticationToken);

        String token = jwtTokenUtil.generateToken(user);
        return new LoginResponse(
                user.getId(),
                jwtTokenUtil.generateToken(user),
                String.valueOf(user.getRole().getId()) // ✅ chuyển roleId thành chuỗi
        );
    }

    @Override
    public Page<UserResponse> getAllUser(PageRequest pageRequest) {
        return userRepository
                .findAll(pageRequest)
                .map(UserResponse::from);
    }


    @Override
    public User updateUser(Long userId, UserUpdateDTO userUpdateDTO) throws DataNotFoundException {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        existingUser.setEmail(userUpdateDTO.getEmail());
        existingUser.setUsername(userUpdateDTO.getUserName());
        existingUser.setFullName(userUpdateDTO.getFullName());
        existingUser.setPhone(userUpdateDTO.getPhone());
        existingUser.setDateOfBirth(userUpdateDTO.getDateOfBirth());
        existingUser.setAddress(userUpdateDTO.getAddress());
        existingUser.setHeightCm(userUpdateDTO.getHeight());
        existingUser.setSex(userUpdateDTO.getSex());
        existingUser.setWeightKg(userUpdateDTO.getWeight());

        return userRepository.save(existingUser);
    }
    @Override
    public User getUserById(Long userId) throws DataNotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy người dùng với ID: " + userId));
    }

}
