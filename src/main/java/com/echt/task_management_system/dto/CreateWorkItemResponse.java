package com.echt.task_management_system.dto;

import java.util.UUID;

public record CreateWorkItemResponse(
        UUID id,
        String itemKey,
        String assignee,
        String reporter,
        String message
) {
}
