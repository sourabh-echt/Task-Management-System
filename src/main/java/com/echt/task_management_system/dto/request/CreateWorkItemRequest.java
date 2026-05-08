package com.echt.task_management_system.dto.request;
import com.echt.task_management_system.entity.WorkItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateWorkItemRequest {

    @NotNull(message = "Project ID is required")
    private UUID projectId;

    /** Optional — if null the item goes to the backlog */
    private UUID sprintId;

    @NotNull(message = "Work type is required")
    private WorkItem.WorkType workType;

    @NotBlank(message = "Summary is required")
    @Size(max = 255, message = "Summary must be at most 255 characters")
    private String summary;

    @Size(max = 5000, message = "Description must be at most 5000 characters")
    private String description;

    private WorkItem.Priority priority = WorkItem.Priority.MEDIUM;

    private UUID assigneeId;

    private UUID reporterId;

    private Integer storyPoints;

    private LocalDate dueDate;
}
