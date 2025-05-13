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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
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
                .active(true)
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

        if (!user.isActive()) {
            String banMessage = user.getBanReason() != null && !user.getBanReason().isEmpty()
                    ? "Tài khoản đã bị vô hiệu hóa. Lý do: " + user.getBanReason() + ". Vui lòng liên hệ admin để được hỗ trợ."
                    : "Tài khoản đã bị vô hiệu hóa. Vui lòng liên hệ admin để được hỗ trợ.";
            throw new DataNotFoundException(banMessage);
        }

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

    public void deleteUser(Long userId, String banReason) {
        logger.info("Deleting user with ID: {}, banReason: {}", userId, banReason);
        User user = null;
        try {
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new DataNotFoundException("User not found"));
        } catch (DataNotFoundException e) {
            throw new RuntimeException(e);
        }
        user.setActive(false);

        if (banReason != null && !banReason.trim().isEmpty()) {
            user.setBanReason(banReason.trim());
        } else {
            logger.warn("Ban reason is null or empty for user ID: {}", userId);
            user.setBanReason("Không có lý do cụ thể");
        }
        userRepository.save(user);
        logger.info("User ID: {} banned successfully with banReason: {}", userId, user.getBanReason());
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
