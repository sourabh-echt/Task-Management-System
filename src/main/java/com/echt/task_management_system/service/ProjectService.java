package com.echt.task_management_system.service;

import com.echt.task_management_system.dto.CreateProjectRequest;
import com.echt.task_management_system.dto.CreateProjectResponse;
import com.echt.task_management_system.dto.ProjectResponse;
import com.echt.task_management_system.entity.Project;
import com.echt.task_management_system.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Transactional
    public CreateProjectResponse create(CreateProjectRequest request) {
        String normalizedKey = request.key().trim().toUpperCase();
        String normalizedName = request.name().trim();
        String normalizedDescription = request.description() == null ? null : request.description().trim();

        if (projectRepository.existsByKeyIgnoreCase(normalizedKey)) {
            throw new IllegalArgumentException("Project key already exists");
        }

        Project project = Project.builder()
                .key(normalizedKey)
                .name(normalizedName)
                .description(normalizedDescription)
                .owner(null)
                .build();

        try {
            Project saved = projectRepository.save(project);
            log.info("Created project id={} key={}", saved.getId(), saved.getKey());
            return new CreateProjectResponse(saved.getId(), saved.getKey(), saved.getName(), "Project created successfully");
        } catch (DataIntegrityViolationException ex) {
            // In case of race condition / DB unique constraint.
            throw new IllegalArgumentException("Project key already exists");
        }
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getAll() {
        return projectRepository.findAllByOrderByNameAsc()
                .stream()
                .map(p -> new ProjectResponse(p.getId(), p.getKey(), p.getName()))
                .toList();
    }
}

