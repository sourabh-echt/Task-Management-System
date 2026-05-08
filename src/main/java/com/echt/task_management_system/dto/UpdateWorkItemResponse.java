package com.echt.task_management_system.dto;

import com.echt.task_management_system.entity.WorkItem;

import java.util.UUID;

public record UpdateWorkItemResponse(
        UUID id,
        String itemKey,
        WorkItem.WorkItemStatus status,
        WorkItem.Priority priority,
        String assignee,
        String message
) {
}
