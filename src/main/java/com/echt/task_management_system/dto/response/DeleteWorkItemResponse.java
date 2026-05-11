package com.echt.task_management_system.dto.response;

import java.util.UUID;

public record DeleteWorkItemResponse(
        UUID id,
        String itemKey,
        String message
) {
}
