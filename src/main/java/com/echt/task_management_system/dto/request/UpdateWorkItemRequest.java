package com.echt.task_management_system.dto.request;

import com.echt.task_management_system.entity.WorkItem;

import jakarta.validation.constraints.Size;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Partial update for a work item — PATCH /work-items/{id}
 * Used for status drag-and-drop (board column changes) and inline edits.
 * All fields optional.
 */
@Data
public class UpdateWorkItemRequest {

    @Size(max = 255, message = "Summary must be at most 255 characters")
    private String summary;

    @Size(max = 5000, message = "Description must be at most 5000 characters")
    private String description;

    private WorkItem.WorkItemStatus status;

    private WorkItem.Priority priority;

    private UUID assigneeId;

    private Integer storyPoints;

    private LocalDate dueDate;

    /** null = move to backlog */
    private UUID sprintId;

    private WorkItem.WorkType workType;
}