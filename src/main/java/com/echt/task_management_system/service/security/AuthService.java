package com.echt.task_management_system.service.security;

import com.echt.task_management_system.dto.request.LoginRequest;
import com.echt.task_management_system.dto.request.UserRegisterationDto;
import com.echt.task_management_system.dto.response.JwtResponse;
import com.echt.task_management_system.entity.User;
import com.echt.task_management_system.enums.Role;
import com.echt.task_management_system.repository.UserRepository;
import com.echt.task_management_system.security.CustomUserDetails;
import com.echt.task_management_system.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public String register(UserRegisterationDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + request.getEmail());
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken: " + request.getUsername());
        }

        Role role = request.getRole() != null
                ? request.getRole()
                : Role.VIEWER;

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))  // BCrypt hash
                .displayName(request.getDisplayName())
                .role(role)
                .build();

        userRepository.save(user);

        return "User '" + request.getUsername() + "' registered successfully with role "
                + role.name() + ".";
    }

    public JwtResponse login(LoginRequest request) {
        // AuthenticationManager validates credentials via CustomUserDetailsService
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("VIEWER");

        String token = jwtService.generateToken(userDetails, userDetails.getUserId(), role);

        return new JwtResponse(token, role);
    }
}
