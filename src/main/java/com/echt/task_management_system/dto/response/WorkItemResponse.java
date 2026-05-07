package com.echt.task_management_system.dto.response;

import com.echt.task_management_system.entity.WorkItem;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Full detail view of a work item.
 */
@Data
@Builder
public class WorkItemResponse {
    private UUID id;
    private String itemKey;
    private WorkItem.WorkType workType;
    private String summary;
    private String description;
    private WorkItem.WorkItemStatus status;
    private WorkItem.Priority priority;
    private UUID projectId;
    private String projectKey;
    private UUID sprintId;
    private String sprintName;
    private UserSummaryResponse assignee;
    private UserSummaryResponse reporter;
    private Integer storyPoints;
    private LocalDate dueDate;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
