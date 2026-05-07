package com.echt.task_management_system.controller;

import com.echt.task_management_system.dto.CreateWorkItemRequest;
import com.echt.task_management_system.dto.CreateWorkItemResponse;
import com.echt.task_management_system.service.CreateWorkItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

