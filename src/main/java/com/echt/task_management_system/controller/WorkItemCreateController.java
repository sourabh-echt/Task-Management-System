package com.echt.task_management_system.controller;

import com.echt.task_management_system.dto.request.CreateWorkItemRequest;
import com.echt.task_management_system.dto.request.UpdateWorkItemRequest;
import com.echt.task_management_system.dto.response.ApiResponse;
import com.echt.task_management_system.dto.response.CreateWorkItemResponse;
import com.echt.task_management_system.dto.response.DeleteWorkItemResponse;
import com.echt.task_management_system.dto.response.UpdateWorkItemResponse;
import com.echt.task_management_system.dto.response.WorkItemListResponse;
import com.echt.task_management_system.service.CreateWorkItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/work-items")
@RequiredArgsConstructor
@Slf4j
public class WorkItemCreateController {

    private final CreateWorkItemService createWorkItemService;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateWorkItemResponse>> create(@Valid @RequestBody CreateWorkItemRequest request) {
        log.debug("POST /work-items spaceId={}", request.getSpaceId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(createWorkItemService.create(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<WorkItemListResponse>>> getAll() {
        log.debug("GET /work-items");
        return ResponseEntity.ok(ApiResponse.ok(createWorkItemService.getAll()));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UpdateWorkItemResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateWorkItemRequest request
    ) {
        log.debug("PATCH /work-items/{} status={} priority={} assigneeId={}",
                id, request.getStatus(), request.getPriority(), request.getAssigneeId());
        return ResponseEntity.ok(ApiResponse.ok(createWorkItemService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<DeleteWorkItemResponse>> delete(@PathVariable UUID id) {
        log.debug("DELETE /work-items/{}", id);
        return ResponseEntity.ok(ApiResponse.ok(createWorkItemService.delete(id)));
    }

}
