package com.echt.task_management_system.dto.request;

import com.echt.task_management_system.entity.WorkItem;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class    CreateWorkItemRequest {

    /** Required Space ID */
    @NotNull(message = "Space is required")
    private UUID spaceId;

    /** Optional — if null the item goes to the backlog */
    private UUID sprintId;

    private UUID parentId;

    private UUID teamId;

    @NotNull(message = "Work type is required")
    private WorkItem.WorkType workType;

    @NotNull(message = "Status is required")
    private WorkItem.WorkItemStatus status;

    @NotBlank(message = "Summary is required")
    @Size(
        min = 5,
        max = 255,
        message = "Summary must be between 5 and 255 characters"
    )
    private String summary;

    @Size(
        max = 5000,
        message = "Description must be at most 5000 characters"
    )
    private String description;

    private WorkItem.Priority priority =
            WorkItem.Priority.MEDIUM;

    private UUID assigneeId;

    @NotNull(message = "Reporter ID is required")
    private UUID reporterId;

    private Integer storyPoints;

    private List<@Size(max = 100, message = "Label must be at most 100 characters") String> labels;

    private LocalDate startDate;

    private LocalDate dueDate;
}
