package com.echt.task_management_system.dto.response;


import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserSummaryResponse {
    private UUID id;
    private String username;
    private String displayName;
    private String avatarUrl;
}
