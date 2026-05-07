package com.echt.task_management_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

// ─── Create Sprint ────────────────────────────────────────────────────────────
/**
 * Used by Dhananjay's POST /projects/{projectId}/sprints endpoint.
 */
@Data
public class CreateSprintRequest {

    @NotNull(message = "Project ID is required")
    private UUID projectId;

    @NotBlank(message = "Sprint name is required")
    @Size(max = 100, message = "Sprint name must be at most 100 characters")
    private String name;

    @Size(max = 500, message = "Sprint goal must be at most 500 characters")
    private String goal;

    private LocalDate startDate;
    private LocalDate endDate;
}
