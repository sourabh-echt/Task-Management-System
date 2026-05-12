package com.echt.task_management_system.controller.security;

import com.echt.task_management_system.dto.request.LoginRequest;
import com.echt.task_management_system.dto.request.UserRegisterationDto;
import com.echt.task_management_system.dto.response.JwtResponse;
import com.echt.task_management_system.service.security.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "APIs for login and user registration")
public class AuthController {

    private final AuthService authService;
    
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate with email and password to receive a JWT token.")
    @SecurityRequirements
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Register user (Admin only)",
            description = "Only ADMIN users can register new accounts. Role defaults to VIEWER when omitted. Role must be one of ADMIN, PROJECT_MANAGER, DEVELOPER, TESTER, VIEWER.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<String> register(@Valid @RequestBody UserRegisterationDto request) {
        return ResponseEntity.ok(authService.register(request));
    }
}
