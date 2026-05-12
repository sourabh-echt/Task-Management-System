package com.echt.task_management_system.dto.response;

import com.echt.task_management_system.entity.WorkItem;

import java.time.LocalDate;
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
        LocalDate dueDate,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
