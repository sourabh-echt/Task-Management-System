package com.echt.task_management_system.dto.response;

import java.util.UUID;

public record CreateProjectResponse(
        UUID id,
        String key,
        String name,
        String message
) {
}

