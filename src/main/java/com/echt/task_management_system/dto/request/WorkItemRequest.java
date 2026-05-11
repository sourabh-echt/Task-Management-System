package com.echt.task_management_system.dto.request;

import java.time.LocalDate;
import java.util.UUID;

import com.echt.task_management_system.entity.WorkItem;

import lombok.Data;

@Data
public class WorkItemRequest {
      private UUID projectId;
    private UUID sprintId;

    private String itemKey;

    private WorkItem.WorkType workType;

    private String summary;
    private String description;

    private WorkItem.WorkItemStatus status;

    private WorkItem.Priority priority;

    private UUID assigneeId;
    private UUID reporterId;

    private Integer storyPoints;
    
    private LocalDate dueDate;
}
