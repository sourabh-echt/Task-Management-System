package com.echt.task_management_system.dto.response;

import java.util.UUID;

public record SpaceResponse(
        UUID id,
        String name
) {
}
