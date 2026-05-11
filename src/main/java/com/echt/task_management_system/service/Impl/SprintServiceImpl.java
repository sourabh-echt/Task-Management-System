package com.echt.task_management_system.service.Impl;

import com.echt.task_management_system.service.SprintService;
import com.echt.task_management_system.dto.request.CreateSprintRequest;
import com.echt.task_management_system.dto.request.UpdateSprintRequest;
import com.echt.task_management_system.dto.response.SprintResponse;
import com.echt.task_management_system.entity.Project;
import com.echt.task_management_system.entity.Sprint;
import com.echt.task_management_system.entity.WorkItem;
import com.echt.task_management_system.mapper.SprintMapper;
import com.echt.task_management_system.repository.ProjectRepository;
import com.echt.task_management_system.repository.SprintRepository;
import com.echt.task_management_system.repository.WorkItemRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SprintServiceImpl implements SprintService {

    private final SprintRepository sprintRepository;
    private final WorkItemRepository workItemRepository;
    private final ProjectRepository projectRepository;
    private final SprintMapper sprintMapper;

    @Override
    public SprintResponse createSprint(
            UUID projectId,
            CreateSprintRequest request) {

        Project project = findProject(projectId);

        Sprint sprint = Sprint.builder()
                .project(project)
                .name(request.getName())
                .goal(request.getGoal())
                .status(Sprint.SprintStatus.PLANNED)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        sprintRepository.save(sprint);

        return sprintMapper.toResponse(sprint);
    }

    @Override
    @Transactional(readOnly = true)
    public SprintResponse getSprintById(UUID sprintId) {

        Sprint sprint = findSprintWithWorkItems(sprintId);

        return sprintMapper.toResponse(sprint);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SprintResponse> getSprintByProjectId(UUID projectId) {

        return sprintRepository.findByProjectIdOrderByCreatedAtAsc(projectId)
                .stream()
                .map(sprintMapper::toResponse)
                .toList();
    }

    @Override
    public SprintResponse updateSprint(
            UUID sprintId,
            UpdateSprintRequest request) {

        Sprint sprint = findSprint(sprintId);

        if (request.getName() != null) {
            sprint.setName(request.getName());
        }

        if (request.getGoal() != null) {
            sprint.setGoal(request.getGoal());
        }

        if (request.getStartDate() != null) {
            sprint.setStartDate(request.getStartDate());
        }

        if (request.getEndDate() != null) {
            sprint.setEndDate(request.getEndDate());
        }

        return sprintMapper.toResponse(sprint);
    }

    @Override
    public SprintResponse startSprint(
            UUID sprintId,
            LocalDate startDate,
            LocalDate endDate) {

        Sprint sprint = findSprint(sprintId);

        boolean hasActiveSprint = sprintRepository.existsByProjectIdAndStatus(
                sprint.getProject().getId(),
                Sprint.SprintStatus.ACTIVE);

        if (hasActiveSprint) {
            throw new IllegalStateException(
                    "Project already has an ACTIVE sprint.");
        }

        sprint.start(startDate, endDate);

        return sprintMapper.toResponse(sprint);
    }

    @Override
    public SprintResponse completeSprint(UUID sprintId) {

        Sprint sprint = findSprint(sprintId);

        sprint.complete();

        return sprintMapper.toResponse(sprint);
    }

    @Override
    public void deleteSprint(UUID sprintId) {

        Sprint sprint = findSprintWithWorkItems(sprintId);

        // Move work items back to backlog
        sprint.getWorkItems()
                .forEach(item -> item.setSprint(null));

        sprintRepository.delete(sprint);
    }

    @Override
    @Transactional(readOnly = true)
    public SprintResponse getBacklog(UUID projectId) {

        Project project = findProject(projectId);

        List<WorkItem> workItems = workItemRepository.findBacklogItems(projectId);

        return sprintMapper.buildBacklogResponse(
                project,
                workItems);
    }

    private Project findProject(UUID projectId) {

        return projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Project not found: " + projectId));
    }

    private Sprint findSprint(UUID sprintId) {

        return sprintRepository.findById(sprintId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Sprint not found: " + sprintId));
    }

    private Sprint findSprintWithWorkItems(UUID sprintId) {

        return sprintRepository.findByIdWithWorkItems(sprintId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Sprint not found: " + sprintId));
    }
}