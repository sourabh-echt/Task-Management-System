package com.echt.task_management_system.dto;

import com.echt.task_management_system.entity.WorkItem;

import java.time.OffsetDateTime;
import java.util.UUID;

public record WorkItemListResponse(
        UUID id,
        String itemKey,
        WorkItem.WorkType workType,
        String summary,
        String assignee,
        String reporter,
        WorkItem.Priority priority,
        WorkItem.WorkItemStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
