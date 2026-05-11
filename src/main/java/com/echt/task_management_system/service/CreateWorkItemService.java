package com.echt.task_management_system.service;

import com.echt.task_management_system.dto.CreateWorkItemRequest;
import com.echt.task_management_system.dto.CreateWorkItemResponse;
import com.echt.task_management_system.dto.DeleteWorkItemResponse;
import com.echt.task_management_system.dto.UpdateWorkItemRequest;
import com.echt.task_management_system.dto.UpdateWorkItemResponse;
import com.echt.task_management_system.dto.WorkItemListResponse;
import com.echt.task_management_system.entity.Project;
import com.echt.task_management_system.entity.User;
import com.echt.task_management_system.entity.WorkItem;
import com.echt.task_management_system.repository.ProjectRepository;
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

    @Transactional
    public CreateWorkItemResponse create(CreateWorkItemRequest request) {
        log.debug("Create work item request received spaceId={} workType={} status={} assigneeId={} reporterId={}",
                request.spaceId(), request.workType(), request.status(), request.assigneeId(), request.reporterId());
        Project project = projectRepository.findById(request.spaceId())
                .orElseThrow(() -> new EntityNotFoundException("Project not found for id=" + request.spaceId()));
        User assignee = resolveUser(request.assigneeId(), "Assignee");
        User reporter = resolveUser(request.reporterId(), "Reporter");

        String itemKey = generateUniqueItemKey(project.getKey());

        WorkItem workItem = WorkItem.builder()
                .project(project)
                .sprint(null) // backlog
                .itemKey(itemKey)
                .workType(request.workType())
                .summary(request.summary())
                .description(null)
                .status(request.status())
                .priority(WorkItem.Priority.MEDIUM)
                .assignee(assignee)
                .reporter(reporter)
                .storyPoints(null)
                .dueDate(null)
                .build();

        WorkItem saved = workItemRepository.save(workItem);
        log.info("Created work item id={} itemKey={} projectKey={} assigneeId={} reporterId={}",
                saved.getId(), saved.getItemKey(), project.getKey(), request.assigneeId(), request.reporterId());
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
                workItem.getCreatedAt(),
                workItem.getUpdatedAt()
        );
    }

    @Transactional
    public UpdateWorkItemResponse update(UUID workItemId, UpdateWorkItemRequest request) {
        WorkItem workItem = workItemRepository.findById(workItemId)
                .orElseThrow(() -> new EntityNotFoundException("Work item not found for id=" + workItemId));

        WorkItem.WorkItemStatus status = parseStatus(request.status());
        WorkItem.Priority priority = parsePriority(request.priority());
        UUID assigneeId = parseUuid(request.assigneeId(), "assigneeId");

        if (status != null) {
            workItem.setStatus(status);
        }
        if (priority != null) {
            workItem.setPriority(priority);
        }
        if (assigneeId != null) {
            workItem.setAssignee(resolveUser(assigneeId, "Assignee"));
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

    private WorkItem.WorkItemStatus parseStatus(String value) {
        String normalized = normalize(value);
        if (normalized == null) {
            return null;
        }
        if ("TODO".equals(normalized)) {
            return WorkItem.WorkItemStatus.TO_DO;
        }
        try {
            return WorkItem.WorkItemStatus.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("status must be one of TO_DO, TODO, IN_PROGRESS, IN_REVIEW, DONE");
        }
    }

    private WorkItem.Priority parsePriority(String value) {
        String normalized = normalize(value);
        if (normalized == null) {
            return null;
        }
        try {
            return WorkItem.Priority.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("priority must be one of LOWEST, LOW, MEDIUM, HIGH, HIGHEST");
        }
    }

    private UUID parseUuid(String value, String fieldName) {
        String normalized = normalize(value);
        if (normalized == null) {
            return null;
        }
        try {
            return UUID.fromString(normalized);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(fieldName + " must be a valid UUID");
        }
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim().toUpperCase();
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
}
