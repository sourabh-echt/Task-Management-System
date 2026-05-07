package com.echt.task_management_system.service;

import com.echt.task_management_system.dto.CreateWorkItemRequest;
import com.echt.task_management_system.dto.CreateWorkItemResponse;
import com.echt.task_management_system.entity.Project;
import com.echt.task_management_system.entity.WorkItem;
import com.echt.task_management_system.repository.ProjectRepository;
import com.echt.task_management_system.repository.WorkItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateWorkItemService {

    private final ProjectRepository projectRepository;
    private final WorkItemRepository workItemRepository;

    public CreateWorkItemResponse create(CreateWorkItemRequest request) {
        log.debug("Create work item request received spaceId={} workType={} status={}",
                request.spaceId(), request.workType(), request.status());
        Project project = projectRepository.findById(request.spaceId())
                .orElseThrow(() -> new EntityNotFoundException("Project not found for id=" + request.spaceId()));

        String itemKey = generateUniqueItemKey(project.getKey());

        WorkItem workItem = WorkItem.builder()
                .project(project)
                .sprint(null) // backlog
                .itemKey(itemKey)
                .workType(request.workType())
                .summary(request.summary())
                .description(null)
                .status(request.status())
                .priority(WorkItem.Priority.MEDIUM) // required by DB
                .assignee(null)
                .reporter(null)
                .storyPoints(null)
                .dueDate(null)
                .build();

        WorkItem saved = workItemRepository.save(workItem);
        log.info("Created work item id={} itemKey={} projectKey={}", saved.getId(), saved.getItemKey(), project.getKey());
        return new CreateWorkItemResponse(saved.getId(), saved.getItemKey(), "Work item created successfully");
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

