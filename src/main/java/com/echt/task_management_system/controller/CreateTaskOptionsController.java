package com.echt.task_management_system.controller;

import com.echt.task_management_system.dto.response.CreateTaskOptionsResponse;
import com.echt.task_management_system.service.CreateTaskOptionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/create-task/options")
@RequiredArgsConstructor
public class CreateTaskOptionsController {

    private final CreateTaskOptionsService createTaskOptionsService;

    @GetMapping
    public CreateTaskOptionsResponse getCreateTaskOptions() {
        return createTaskOptionsService.getOptions();
    }
}
