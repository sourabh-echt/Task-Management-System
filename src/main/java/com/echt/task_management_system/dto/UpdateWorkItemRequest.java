package com.echt.task_management_system.dto;

public record UpdateWorkItemRequest(
        String status,
        String priority,
        String assigneeId
) {
}
