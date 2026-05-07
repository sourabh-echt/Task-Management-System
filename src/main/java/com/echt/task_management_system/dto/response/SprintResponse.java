package com.echt.task_management_system.dto.response;


import com.echt.task_management_system.entity.Sprint;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Sprint response — full detail including work item summaries.
 * Returned by Dhananjay's GET /sprints/{id} and GET /projects/{id}/sprints
 */
@Data
@Builder
public class SprintResponse {
    private UUID id;
    private String name;
    private String goal;
    private Sprint.SprintStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private UUID projectId;
    private String projectKey;
    private List<WorkItemSummaryResponse> workItems;
    private int totalItems;
    private int doneItems;
    private int inProgressItems;
    private int toDoItems;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
