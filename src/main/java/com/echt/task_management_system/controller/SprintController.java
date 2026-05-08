package com.echt.task_management_system.controller;

import com.echt.task_management_system.Service.SprintService;
import com.echt.task_management_system.dto.request.CreateSprintRequest;
import com.echt.task_management_system.dto.request.UpdateSprintRequest;
import com.echt.task_management_system.dto.response.SprintResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Sprint APIs", description = "Create, view, update, start, complete, delete, and inspect sprint backlog data.")
public class SprintController {

        private final SprintService sprintService;

        @PostMapping("/projects/{projectId}/sprints")
        @Operation(summary = "Create sprint", description = "Creates a planned sprint for the selected project.")
        public ResponseEntity<SprintResponse> createSprint(

                        @Parameter(description = "Project ID that will own the sprint", required = true) @PathVariable UUID projectId,

                        @Valid @RequestBody CreateSprintRequest request) {

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(sprintService.createSprint(projectId, request));
        }

        @GetMapping("/sprints/{sprintId}")
        @Operation(summary = "Get sprint by ID", description = "Returns sprint details, including work item summaries and status counts.")
        public ResponseEntity<SprintResponse> getSprintById(
                        @Parameter(description = "Sprint ID to fetch", required = true) @PathVariable UUID sprintId) {
                return ResponseEntity.ok(sprintService.getSprintById(sprintId));
        }

        @GetMapping("/projects/{projectId}/sprints")
        @Operation(summary = "List project sprints", description = "Returns all sprints for a project ordered by creation time.")
        public ResponseEntity<List<SprintResponse>> getSprintsByProjectId(
                        @Parameter(description = "Project ID whose sprints should be listed", required = true) @PathVariable UUID projectId) {
                return ResponseEntity.ok(sprintService.getSprintByProjectId(projectId));
        }

        @PatchMapping("/sprints/{sprintId}")
        @Operation(summary = "Update sprint", description = "Partially updates sprint name, goal, start date, or end date.")
        public ResponseEntity<SprintResponse> updateSprint(
                        @Parameter(description = "Sprint ID to update", required = true) @PathVariable UUID sprintId,
                        @Valid @RequestBody UpdateSprintRequest request) {
                return ResponseEntity.ok(sprintService.updateSprint(sprintId, request));
        }

        @PostMapping("/sprints/{sprintId}/start")
        @Operation(summary = "Start sprint", description = "Moves a planned sprint to active status and applies the supplied date range.")
        public ResponseEntity<SprintResponse> startSprint(
                        @Parameter(description = "Sprint ID to start", required = true) @PathVariable UUID sprintId,
                        @Parameter(description = "Sprint start date in ISO format, for example 2026-05-07", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                        @Parameter(description = "Sprint end date in ISO format, for example 2026-05-21", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
                return ResponseEntity.ok(sprintService.startSprint(sprintId, startDate, endDate));
        }

        @PostMapping("/sprints/{sprintId}/complete")
        @Operation(summary = "Complete sprint", description = "Moves an active sprint to completed status.")
        public ResponseEntity<SprintResponse> completeSprint(
                        @Parameter(description = "Sprint ID to complete", required = true) @PathVariable UUID sprintId) {
                return ResponseEntity.ok(sprintService.completeSprint(sprintId));
        }

        @DeleteMapping("/sprints/{sprintId}")
        @Operation(summary = "Delete sprint", description = "Deletes the sprint identified by the supplied sprint ID.")
        public ResponseEntity<Void> deleteSprint(
                        @Parameter(description = "Sprint ID to delete", required = true) @PathVariable UUID sprintId) {
                sprintService.deleteSprint(sprintId);
                return ResponseEntity.noContent().build();
        }

        @GetMapping("/projects/{projectId}/backlog")
        @Operation(summary = "Get project backlog", description = "Returns work items in the selected project that are not assigned to any sprint.")
        public ResponseEntity<SprintResponse> getBacklog(
                        @Parameter(description = "Project ID whose backlog should be returned", required = true) @PathVariable UUID projectId) {
                return ResponseEntity.ok(sprintService.getBacklog(projectId));
        }
}
