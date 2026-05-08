package com.echt.task_management_system.dto;

import com.echt.task_management_system.entity.WorkItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateWorkItemRequest(
        @NotNull(message = "Space is required")
        UUID spaceId,

        @NotNull(message = "Work Type is required")
        WorkItem.WorkType workType,

        @NotNull(message = "Status is required")
        WorkItem.WorkItemStatus status,

        @NotBlank(message = "Summary is required")
        @Size(min = 5, message = "Summary must be at least 5 characters")
        String summary,

        UUID assigneeId,

        UUID reporterId
) {
}
