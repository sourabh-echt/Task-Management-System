package com.echt.task_management_system.controller;

import com.echt.task_management_system.service.WorkItemService;
import com.echt.task_management_system.dto.request.CreateWorkItemRequest;
import com.echt.task_management_system.dto.request.UpdateWorkItemRequest;
import com.echt.task_management_system.dto.response.ApiResponse;
import com.echt.task_management_system.dto.response.BoardResponse;
import com.echt.task_management_system.dto.response.WorkItemResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class WorkItemController {

    private final WorkItemService workItemService;

    @PostMapping("/projects/{projectId}/work-items")
    @Operation(summary = "Create work item", description = """
            Creates a new work item inside a project.
            If sprintId is null, the item is placed in backlog.
            """)
    public ResponseEntity<ApiResponse<WorkItemResponse>> createWorkItem(

            @Parameter(description = "Project ID", required = true) @PathVariable UUID projectId,

            @Valid @RequestBody CreateWorkItemRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.ok(workItemService.createWorkItem(projectId, request))
        );
    }

    @GetMapping("/work-items/{workItemId}")
    @Operation(summary = "Get work item", description = "Returns detailed information about a work item.")
    public ResponseEntity<ApiResponse<WorkItemResponse>> getWorkItemById(

            @Parameter(description = "Work item ID", required = true) @PathVariable UUID workItemId) {

        return ResponseEntity.ok(ApiResponse.ok(workItemService.getWorkItemById(workItemId)));
    }

    @PatchMapping("/work-items/{workItemId}")
    @Operation(summary = "Update work item", description = """
            Partially updates a work item.
            Supports sprint movement, backlog movement,
            reassignment, and board status updates.
            """)
    public ResponseEntity<ApiResponse<WorkItemResponse>> updateWorkItem(

            @Parameter(description = "Work item ID", required = true) @PathVariable UUID workItemId,

            @Valid @RequestBody UpdateWorkItemRequest request) {

        return ResponseEntity.ok(ApiResponse.ok(workItemService.updateWorkItem(workItemId, request)));
    }

    @DeleteMapping("/work-items/{workItemId}")
    @Operation(summary = "Delete work item", description = "Deletes a work item permanently.")
    public ResponseEntity<ApiResponse<Void>> deleteWorkItem(

            @Parameter(description = "Work item ID", required = true) @PathVariable UUID workItemId) {

        workItemService.deleteWorkItem(workItemId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.ok(null));
    }

    @PutMapping("/work-items/{workItemId}/sprint/{sprintId}")
    @Operation(summary = "Assign work item to sprint", description = """
            Assigns a work item to a sprint.
            Can also move item between sprints.
            """)
    public ResponseEntity<ApiResponse<WorkItemResponse>> assignToSprint(

            @Parameter(description = "Work item ID", required = true) @PathVariable UUID workItemId,

            @Parameter(description = "Sprint ID", required = true) @PathVariable UUID sprintId) {

        return ResponseEntity.ok(ApiResponse.ok(workItemService.assignToSprint(workItemId, sprintId)));
    }

    @PutMapping("/work-items/{workItemId}/backlog")
    @Operation(summary = "Move work item to backlog", description = """
            Removes sprint assignment from a work item.
            """)
    public ResponseEntity<ApiResponse<WorkItemResponse>> moveToBacklog(

            @Parameter(description = "Work item ID", required = true) @PathVariable UUID workItemId) {

        return ResponseEntity.ok(ApiResponse.ok(workItemService.moveToBacklog(workItemId)));
    }

      @Operation(summary = "Get the task status", description = """
           List all the task status in the project.
            """)
    @GetMapping("/projects/{projectId}/board")
public ResponseEntity<ApiResponse<BoardResponse>> getBoard(
        @PathVariable UUID projectId
) {

    return ResponseEntity.ok(ApiResponse.ok(workItemService.getBoard(projectId)));
}
}
