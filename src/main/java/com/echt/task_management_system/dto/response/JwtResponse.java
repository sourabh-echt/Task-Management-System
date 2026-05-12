package com.echt.task_management_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String tokenType = "Bearer";
    private String role;

    public JwtResponse(String token, String role) {
        this.token = token;
        this.role = role;
    }
}
