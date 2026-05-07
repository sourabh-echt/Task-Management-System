package com.echt.task_management_system.Service.Impl;

import com.echt.task_management_system.Service.SprintService;
import com.echt.task_management_system.dto.request.CreateSprintRequest;
import com.echt.task_management_system.dto.request.UpdateSprintRequest;
import com.echt.task_management_system.dto.response.SprintResponse;
import com.echt.task_management_system.dto.response.UserSummaryResponse;
import com.echt.task_management_system.dto.response.WorkItemSummaryResponse;
import com.echt.task_management_system.entity.Project;
import com.echt.task_management_system.entity.Sprint;
import com.echt.task_management_system.entity.User;
import com.echt.task_management_system.entity.WorkItem;
import com.echt.task_management_system.repository.SprintRepository;
import jakarta.persistence.EntityManager;
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
    private final EntityManager entityManager;

    @Override
    public CreateSprintRequest createSprint(CreateSprintRequest request) {
        Project project = findProject(request.getProjectId());
        Sprint sprint = Sprint.builder()
                .project(project)
                .name(request.getName())
                .goal(request.getGoal())
                .status(Sprint.SprintStatus.PLANNED)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        sprintRepository.save(sprint);
        return request;
    }

    @Override
    @Transactional(readOnly = true)
    public SprintResponse getSprintById(UUID sprintId) {
        return toResponse(findSprintWithWorkItems(sprintId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SprintResponse> getSprintByProjectId(UUID projectId) {
        return sprintRepository.findByProjectIdOrderByCreatedAtAsc(projectId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public SprintResponse updateSprint(UUID sprintId, UpdateSprintRequest request) {
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
        return toResponse(sprint);
    }

    @Override
    public SprintResponse startSprint(UUID sprintId, LocalDate startDate, LocalDate endDate) {
        Sprint sprint = findSprint(sprintId);
        if (sprintRepository.existsByProjectIdAndStatus(sprint.getProject().getId(), Sprint.SprintStatus.ACTIVE)) {
            throw new IllegalStateException("Project already has an ACTIVE sprint.");
        }
        sprint.start(startDate, endDate);
        return toResponse(sprint);
    }

    @Override
    public SprintResponse completeSprint(UUID sprintId) {
        Sprint sprint = findSprint(sprintId);
        sprint.complete();
        return toResponse(sprint);
    }

    @Override
    public void deleteSprint(UUID sprintId) {
        Sprint sprint = findSprint(sprintId);
        sprintRepository.delete(sprint);
    }

    @Override
    @Transactional(readOnly = true)
    public SprintResponse getBacklog(UUID projectId) {
        Project project = findProject(projectId);
        List<WorkItem> workItems = entityManager.createQuery("""
                        SELECT w FROM WorkItem w
                        LEFT JOIN FETCH w.assignee
                        WHERE w.project.id = :projectId AND w.sprint IS NULL
                        ORDER BY w.createdAt ASC
                        """, WorkItem.class)
                .setParameter("projectId", projectId)
                .getResultList();

        return buildResponse(null, "Backlog", null, null, null, null, project, workItems);
    }

    private Project findProject(UUID projectId) {
        Project project = entityManager.find(Project.class, projectId);
        if (project == null) {
            throw new EntityNotFoundException("Project not found: " + projectId);
        }
        return project;
    }

    private Sprint findSprint(UUID sprintId) {
        return sprintRepository.findById(sprintId)
                .orElseThrow(() -> new EntityNotFoundException("Sprint not found: " + sprintId));
    }

    private Sprint findSprintWithWorkItems(UUID sprintId) {
        return sprintRepository.findByIdWithWorkItems(sprintId)
                .orElseThrow(() -> new EntityNotFoundException("Sprint not found: " + sprintId));
    }

    private SprintResponse toResponse(Sprint sprint) {
        return buildResponse(
                sprint.getId(),
                sprint.getName(),
                sprint.getGoal(),
                sprint.getStatus(),
                sprint.getStartDate(),
                sprint.getEndDate(),
                sprint.getProject(),
                sprint.getWorkItems(),
                sprint.getCreatedAt(),
                sprint.getUpdatedAt());
    }

    private SprintResponse buildResponse(
            UUID id,
            String name,
            String goal,
            Sprint.SprintStatus status,
            LocalDate startDate,
            LocalDate endDate,
            Project project,
            List<WorkItem> workItems) {
        return buildResponse(id, name, goal, status, startDate, endDate, project, workItems, null, null);
    }

    private SprintResponse buildResponse(
            UUID id,
            String name,
            String goal,
            Sprint.SprintStatus status,
            LocalDate startDate,
            LocalDate endDate,
            Project project,
            List<WorkItem> workItems,
            java.time.OffsetDateTime createdAt,
            java.time.OffsetDateTime updatedAt) {
        List<WorkItem> items = workItems == null ? List.of() : workItems;

        return SprintResponse.builder()
                .id(id)
                .name(name)
                .goal(goal)
                .status(status)
                .startDate(startDate)
                .endDate(endDate)
                .projectId(project.getId())
                .projectKey(project.getKey())
                .workItems(items.stream().map(this::toWorkItemSummary).toList())
                .totalItems(items.size())
                .doneItems(countByStatus(items, WorkItem.WorkItemStatus.DONE))
                .inProgressItems(countByStatus(items, WorkItem.WorkItemStatus.IN_PROGRESS))
                .toDoItems(countByStatus(items, WorkItem.WorkItemStatus.TO_DO))
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    private int countByStatus(List<WorkItem> items, WorkItem.WorkItemStatus status) {
        return (int) items.stream()
                .filter(item -> item.getStatus() == status)
                .count();
    }

    private WorkItemSummaryResponse toWorkItemSummary(WorkItem item) {
        return WorkItemSummaryResponse.builder()
                .id(item.getId())
                .itemKey(item.getItemKey())
                .workType(item.getWorkType())
                .summary(item.getSummary())
                .status(item.getStatus())
                .priority(item.getPriority())
                .storyPoints(item.getStoryPoints())
                .assignee(toUserSummary(item.getAssignee()))
                .build();
    }

    private UserSummaryResponse toUserSummary(User user) {
        if (user == null) {
            return null;
        }
        return UserSummaryResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
}