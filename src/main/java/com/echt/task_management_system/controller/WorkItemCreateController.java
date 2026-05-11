package com.echt.task_management_system.controller;

import com.echt.task_management_system.dto.CreateWorkItemRequest;
import com.echt.task_management_system.dto.CreateWorkItemResponse;
import com.echt.task_management_system.dto.DeleteWorkItemResponse;
import com.echt.task_management_system.dto.UpdateWorkItemRequest;
import com.echt.task_management_system.dto.UpdateWorkItemResponse;
import com.echt.task_management_system.dto.WorkItemListResponse;
import com.echt.task_management_system.service.CreateWorkItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public CreateWorkItemResponse create(@Valid @RequestBody CreateWorkItemRequest request) {
        log.debug("POST /work-items spaceId={}", request.spaceId());
        return createWorkItemService.create(request);
    }

    @GetMapping
    public List<WorkItemListResponse> getAll() {
        log.debug("GET /work-items");
        return createWorkItemService.getAll();
    }

    @PatchMapping("/{id}")
    public UpdateWorkItemResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateWorkItemRequest request
    ) {
        log.debug("PATCH /work-items/{} status={} priority={} assigneeId={}",
                id, request.status(), request.priority(), request.assigneeId());
        return createWorkItemService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public DeleteWorkItemResponse delete(@PathVariable UUID id) {
        log.debug("DELETE /work-items/{}", id);
        return createWorkItemService.delete(id);
    }

}
