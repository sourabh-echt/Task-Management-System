package com.echt.task_management_system.controller;

import com.echt.task_management_system.dto.SpaceResponse;
import com.echt.task_management_system.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/spaces")
@RequiredArgsConstructor
@Slf4j
public class SpacesController {

    private final ProjectRepository projectRepository;

    @GetMapping
    public List<SpaceResponse> getSpaces() {
        log.debug("GET /spaces");
        return projectRepository.findAllByOrderByNameAsc()
                .stream()
                .map(project -> new SpaceResponse(project.getId(), project.getName()))
                .toList();
    }
}

