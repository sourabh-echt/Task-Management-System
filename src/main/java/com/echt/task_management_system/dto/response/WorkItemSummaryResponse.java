package com.echt.task_management_system.dto.response;

import com.echt.task_management_system.entity.WorkItem;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/** Lightweight work-item view used inside SprintResponse lists. */
@Data
@Builder
public class WorkItemSummaryResponse {
    private UUID id;
    private String itemKey;
    private WorkItem.WorkType workType;
    private String summary;
    private WorkItem.WorkItemStatus status;
    private WorkItem.Priority priority;
    private Integer storyPoints;
    private UserSummaryResponse assignee;
}
