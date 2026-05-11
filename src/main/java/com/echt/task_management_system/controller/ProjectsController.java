package com.echt.task_management_system.controller;

import com.echt.task_management_system.dto.CreateProjectRequest;
import com.echt.task_management_system.dto.CreateProjectResponse;
import com.echt.task_management_system.dto.ProjectResponse;
import com.echt.task_management_system.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectsController {

    private final ProjectService projectService;

    @PostMapping("/createProject")
    public CreateProjectResponse create(@Valid @RequestBody CreateProjectRequest request) {
        log.debug("Create project request received key={}", request.key());
        return projectService.create(request);
    }

    @GetMapping("/getAllProjects")
    public List<ProjectResponse> getAll() {
        return projectService.getAll();
    }
}

