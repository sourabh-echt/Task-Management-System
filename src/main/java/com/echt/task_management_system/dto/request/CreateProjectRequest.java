package com.echt.task_management_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProjectRequest(
        @NotBlank(message = "Project key is required")
        @Size(max = 10, message = "Project key must be at most 10 characters")
        String key,

        @NotBlank(message = "Project name is required")
        @Size(max = 100, message = "Project name must be at most 100 characters")
        String name,

        @Size(max = 10_000, message = "Description is too long")
        String description
) {
}

