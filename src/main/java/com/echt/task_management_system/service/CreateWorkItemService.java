package com.echt.task_management_system.service;

import com.echt.task_management_system.dto.request.CreateWorkItemRequest;
import com.echt.task_management_system.dto.request.UpdateWorkItemRequest;
import com.echt.task_management_system.dto.response.CreateWorkItemResponse;
import com.echt.task_management_system.dto.response.DeleteWorkItemResponse;
import com.echt.task_management_system.dto.response.ParentOptionResponse;
import com.echt.task_management_system.dto.response.UpdateWorkItemResponse;
import com.echt.task_management_system.dto.response.WorkItemListResponse;
import com.echt.task_management_system.entity.Project;
import com.echt.task_management_system.entity.User;
import com.echt.task_management_system.entity.WorkItem;
import com.echt.task_management_system.repository.ProjectRepository;
import com.echt.task_management_system.repository.SprintRepository;
import com.echt.task_management_system.repository.TeamRepository;
import com.echt.task_management_system.repository.UserRepository;
import com.echt.task_management_system.repository.WorkItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateWorkItemService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final WorkItemRepository workItemRepository;
    private final SprintRepository sprintRepository;
    private final TeamRepository teamRepository;

    @Transactional
    public CreateWorkItemResponse create(CreateWorkItemRequest request) {
        log.debug("Create work item request received spaceId={} workType={} status={} assigneeId={} reporterId={}",
                request.getSpaceId(), request.getWorkType(), request.getStatus(), request.getAssigneeId(), request.getReporterId());

        if (request.getSpaceId() == null) {
            throw new IllegalArgumentException("spaceId is required");
        }

        Project project = projectRepository.findById(request.getSpaceId())
                .orElseThrow(() -> new EntityNotFoundException("Project not found for id=" + request.getSpaceId()));

        User assignee = resolveUser(request.getAssigneeId(), "Assignee");
        User reporter = resolveUser(request.getReporterId(), "Reporter");

        String itemKey = generateUniqueItemKey(project.getKey());

        WorkItem workItem = WorkItem.builder()
                .project(project)
                .sprint(request.getSprintId() == null ? null : sprintRepository.findById(request.getSprintId())
                        .orElseThrow(() -> new EntityNotFoundException("Sprint not found for id=" + request.getSprintId())))
                .parent(resolveParent(request))
                .team(request.getTeamId() == null ? null : teamRepository.findById(request.getTeamId())
                        .orElseThrow(() -> new EntityNotFoundException("Team not found for id=" + request.getTeamId())))
                .itemKey(itemKey)
                .workType(request.getWorkType())
                .summary(request.getSummary())
                .description(request.getDescription())
                .status(request.getStatus() == null ? WorkItem.WorkItemStatus.TO_DO : request.getStatus())
                .priority(request.getPriority() == null ? WorkItem.Priority.MEDIUM : request.getPriority())
                .assignee(assignee)
                .reporter(reporter)
                .storyPoints(request.getStoryPoints())
                .labels(normalizeLabels(request.getLabels()))
                .startDate(request.getStartDate())
                .dueDate(request.getDueDate())
                .build();

        WorkItem saved = workItemRepository.save(workItem);
        log.info("Created work item id={} itemKey={} projectKey={} assigneeId={} reporterId={}",
                saved.getId(), saved.getItemKey(), project.getKey(), request.getAssigneeId(), request.getReporterId());
        return new CreateWorkItemResponse(
                saved.getId(),
                saved.getItemKey(),
                userLabel(saved.getAssignee(), "Unassigned"),
                userLabel(saved.getReporter(), "Unassigned"),
                "Work item created successfully"
        );
    }

    @Transactional(readOnly = true)
    public List<WorkItemListResponse> getAll() {
        return workItemRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toListResponse)
                .toList();
    }

    private WorkItemListResponse toListResponse(WorkItem workItem) {
        return new WorkItemListResponse(
                workItem.getId(),
                workItem.getItemKey(),
                workItem.getWorkType(),
                workItem.getSummary(),
                userLabel(workItem.getAssignee(), "Unassigned"),
                userLabel(workItem.getReporter(), "Unassigned"),
                workItem.getPriority(),
                workItem.getStatus(),
                workItem.getDueDate(),
                workItem.getCreatedAt(),
                workItem.getUpdatedAt()
        );
    }

    @Transactional
    public UpdateWorkItemResponse update(UUID workItemId, UpdateWorkItemRequest request) {
        WorkItem workItem = workItemRepository.findById(workItemId)
                .orElseThrow(() -> new EntityNotFoundException("Work item not found for id=" + workItemId));

        WorkItem.WorkItemStatus status = request.getStatus();
        WorkItem.Priority priority = request.getPriority();
        UUID assigneeId = request.getAssigneeId();

        if (status != null) {
            workItem.setStatus(status);
        }
        if (priority != null) {
            workItem.setPriority(priority);
        }
        if (assigneeId != null) {
            workItem.setAssignee(resolveUser(assigneeId, "Assignee"));
        }
        if (request.getLabels() != null) {
            workItem.setLabels(normalizeLabels(request.getLabels()));
        }
        if (request.getStartDate() != null) {
            workItem.setStartDate(request.getStartDate());
        }
        if (request.getDueDate() != null) {
            workItem.setDueDate(request.getDueDate());
        }
        if (request.getSprintId() != null) {
            workItem.setSprint(sprintRepository.findById(request.getSprintId())
                    .orElseThrow(() -> new EntityNotFoundException("Sprint not found for id=" + request.getSprintId())));
        }
        if (request.getParentId() != null) {
            workItem.setParent(resolveParent(request));
        }
        if (request.getTeamId() != null) {
            workItem.setTeam(teamRepository.findById(request.getTeamId())
                    .orElseThrow(() -> new EntityNotFoundException("Team not found for id=" + request.getTeamId())));
        }

        WorkItem saved = workItemRepository.save(workItem);
        log.info("Updated work item id={} itemKey={} status={} priority={} assigneeId={}",
                saved.getId(), saved.getItemKey(), saved.getStatus(), saved.getPriority(), assigneeId);

        return new UpdateWorkItemResponse(
                saved.getId(),
                saved.getItemKey(),
                saved.getStatus(),
                saved.getPriority(),
                userLabel(saved.getAssignee(), "Unassigned"),
                "Work item updated successfully"
        );
    }

    @Transactional
    public DeleteWorkItemResponse delete(UUID workItemId) {
        WorkItem workItem = workItemRepository.findById(workItemId)
                .orElseThrow(() -> new EntityNotFoundException("Work item not found for id=" + workItemId));

        workItemRepository.delete(workItem);
        log.info("Deleted work item id={} itemKey={}", workItem.getId(), workItem.getItemKey());

        return new DeleteWorkItemResponse(
                workItem.getId(),
                workItem.getItemKey(),
                "Work item deleted successfully"
        );
    }

    private String userLabel(User user, String fallback) {
        if (user == null) {
            return fallback;
        }
        if (user.getDisplayName() != null && !user.getDisplayName().isBlank()) {
            return user.getDisplayName();
        }
        return user.getUsername();
    }

    private User resolveUser(UUID userId, String fieldName) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(fieldName + " user not found for id=" + userId));
    }

    private String generateUniqueItemKey(String projectKey) {
        String prefix = projectKey + "-";
        // Ensure <= 20 chars: key (<=10) + "-" + 6 hex chars.
        String candidate;
        do {
            candidate = prefix + UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        } while (workItemRepository.existsByItemKey(candidate));
        return candidate;
    }

    private WorkItem resolveParent(CreateWorkItemRequest request) {
        if (request.getParentId() == null) {
            return null;
        }
        WorkItem parent = workItemRepository.findById(request.getParentId())
                .orElseThrow(() -> new EntityNotFoundException("Parent work item not found for id=" + request.getParentId()));
        validateParent(request.getWorkType(), parent);
        return parent;
    }

    private WorkItem resolveParent(UpdateWorkItemRequest request) {
        if (request.getParentId() == null) {
            return null;
        }
        WorkItem parent = workItemRepository.findById(request.getParentId())
                .orElseThrow(() -> new EntityNotFoundException("Parent work item not found for id=" + request.getParentId()));
        if (parent.getWorkType() != WorkItem.WorkType.EPIC) {
            throw new IllegalStateException("Story and task parent must be an epic.");
        }
        return parent;
    }

    private void validateParent(WorkItem.WorkType workType, WorkItem parent) {
        if (workType == WorkItem.WorkType.EPIC) {
            throw new IllegalStateException("Epic work items cannot have a parent.");
        }
        if ((workType == WorkItem.WorkType.STORY || workType == WorkItem.WorkType.TASK)
                && parent.getWorkType() != WorkItem.WorkType.EPIC) {
            throw new IllegalStateException("Story and task parent must be an epic.");
        }
    }

    private List<String> normalizeLabels(List<String> labels) {
        if (labels == null) {
            return List.of();
        }
        return labels.stream()
                .filter(label -> label != null && !label.isBlank())
                .map(String::trim)
                .distinct()
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ParentOptionResponse> getParentOptions(WorkItem.WorkType workType) {
        if (workType == WorkItem.WorkType.EPIC) {
            return List.of();
        }
        if (workType != WorkItem.WorkType.STORY && workType != WorkItem.WorkType.TASK) {
            return List.of();
        }
        return workItemRepository.findByWorkTypeOrderByCreatedAtDesc(WorkItem.WorkType.EPIC).stream()
                .map(item -> new ParentOptionResponse(
                        item.getId(),
                        item.getItemKey(),
                        item.getWorkType(),
                        item.getSummary()
                ))
                .toList();
    }
}
