package com.echt.task_management_system.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * Update sprint name / goal / dates — PATCH /sprints/{id}
 * All fields are optional (partial update semantics).
 */
@Data
public class UpdateSprintRequest {

    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String goal;

    private LocalDate startDate;
    private LocalDate endDate;
}
