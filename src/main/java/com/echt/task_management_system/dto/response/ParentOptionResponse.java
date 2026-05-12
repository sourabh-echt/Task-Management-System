package com.echt.task_management_system.dto.response;

import com.echt.task_management_system.entity.WorkItem;

import java.util.UUID;

public record ParentOptionResponse(
        UUID id,
        String itemKey,
        WorkItem.WorkType workType,
        String summary
) {
}
