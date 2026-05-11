package com.echt.task_management_system.service.Impl;

import com.echt.task_management_system.service.WorkItemService;
import com.echt.task_management_system.dto.request.CreateWorkItemRequest;
import com.echt.task_management_system.dto.request.UpdateWorkItemRequest;
import com.echt.task_management_system.dto.response.BoardResponse;
import com.echt.task_management_system.dto.response.WorkItemResponse;
import com.echt.task_management_system.dto.response.WorkItemSummaryResponse;
import com.echt.task_management_system.entity.Project;
import com.echt.task_management_system.entity.Sprint;
import com.echt.task_management_system.entity.User;
import com.echt.task_management_system.entity.WorkItem;
import com.echt.task_management_system.mapper.WorkItemMapper;
import com.echt.task_management_system.repository.ProjectRepository;
import com.echt.task_management_system.repository.SprintRepository;
import com.echt.task_management_system.repository.UserRepository;
import com.echt.task_management_system.repository.WorkItemRepository;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkItemServiceImpl
        implements WorkItemService {

    private final WorkItemRepository workItemRepository;

    private final ProjectRepository projectRepository;

    private final SprintRepository sprintRepository;

    private final UserRepository userRepository;

    private final WorkItemMapper workItemMapper;

    @Override
    public WorkItemResponse createWorkItem(
            UUID projectId,
            CreateWorkItemRequest request
    ) {

        Project project =
                findProject(projectId);

        Sprint sprint =
                getSprintIfPresent(
                        request.getSprintId()
                );

        validateSprintProject(
                project,
                sprint
        );

        User assignee =
                getUserIfPresent(
                        request.getAssigneeId()
                );

        User reporter =
                findUser(
                        request.getReporterId()
                );

        WorkItem workItem =
                WorkItem.builder()

                        .project(project)

                        .sprint(sprint)

                        .itemKey(
                                generateItemKey(project)
                        )

                        .workType(
                                request.getWorkType()
                        )

                        .summary(
                                request.getSummary()
                        )

                        .description(
                                request.getDescription()
                        )

                        .status(
                                WorkItem.WorkItemStatus.TO_DO
                        )

                        .priority(
                                request.getPriority()
                        )

                        .assignee(assignee)

                        .reporter(reporter)

                        .storyPoints(
                                request.getStoryPoints()
                        )

                        .dueDate(
                                request.getDueDate()
                        )

                        .build();

        workItemRepository.save(workItem);

        return workItemMapper.toResponse(workItem);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkItemResponse getWorkItemById(
            UUID workItemId
    ) {

        WorkItem workItem =
                findWorkItemWithDetails(
                        workItemId
                );

        return workItemMapper.toResponse(workItem);
    }

    @Override
    public WorkItemResponse updateWorkItem(
            UUID workItemId,
            UpdateWorkItemRequest request
    ) {

        WorkItem workItem =
                findWorkItemWithDetails(
                        workItemId
                );

        if (request.getSummary() != null) {
            workItem.setSummary(
                    request.getSummary()
            );
        }

        if (request.getDescription() != null) {
            workItem.setDescription(
                    request.getDescription()
            );
        }

        if (request.getStatus() != null) {
            workItem.setStatus(
                    request.getStatus()
            );
        }

        if (request.getPriority() != null) {
            workItem.setPriority(
                    request.getPriority()
            );
        }

        if (request.getStoryPoints() != null) {
            workItem.setStoryPoints(
                    request.getStoryPoints()
            );
        }

        if (request.getDueDate() != null) {
            workItem.setDueDate(
                    request.getDueDate()
            );
        }

        if (request.getAssigneeId() != null) {

            User assignee =
                    findUser(
                            request.getAssigneeId()
                    );

            workItem.setAssignee(assignee);
        }

        return workItemMapper.toResponse(workItem);
    }

    @Override
    public void deleteWorkItem(
            UUID workItemId
    ) {

        WorkItem workItem =
                findWorkItem(workItemId);

        workItemRepository.delete(workItem);
    }

    @Override
    public WorkItemResponse assignToSprint(
            UUID workItemId,
            UUID sprintId
    ) {

        WorkItem workItem =
                findWorkItem(workItemId);

        Sprint sprint =
                findSprint(sprintId);

        validateSprintAssignment(
                workItem,
                sprint
        );

        workItem.setSprint(sprint);

        return workItemMapper.toResponse(workItem);
    }

    @Override
    public WorkItemResponse moveToBacklog(
            UUID workItemId
    ) {

        WorkItem workItem =
                findWorkItem(workItemId);

        workItem.setSprint(null);

        return workItemMapper.toResponse(workItem);
    }

    @Override
    @Transactional(readOnly = true)
    public BoardResponse getBoard(
            UUID projectId
    ) {

        findProject(projectId);

        List<WorkItem> items =
                workItemRepository.findBoardItems(
                        projectId
                );

        return BoardResponse.builder()

                .toDo(
                        mapByStatus(
                                items,
                                WorkItem.WorkItemStatus.TO_DO
                        )
                )

                .inProgress(
                        mapByStatus(
                                items,
                                WorkItem.WorkItemStatus.IN_PROGRESS
                        )
                )

                .inReview(
                        mapByStatus(
                                items,
                                WorkItem.WorkItemStatus.IN_REVIEW
                        )
                )

                .done(
                        mapByStatus(
                                items,
                                WorkItem.WorkItemStatus.DONE
                        )
                )

                .build();
    }

    private List<WorkItemSummaryResponse> mapByStatus(
            List<WorkItem> items,
            WorkItem.WorkItemStatus status
    ) {

        return items.stream()

                .filter(item ->
                        item.getStatus() == status
                )

                .map(this::toBoardItem)

                .toList();
    }

   private WorkItemSummaryResponse toBoardItem(
        WorkItem item
) {

    return WorkItemSummaryResponse.builder()

            .id(item.getId())

            .itemKey(item.getItemKey())

            .workType(item.getWorkType())

            .summary(item.getSummary())

            .status(item.getStatus())

            .priority(item.getPriority())

            .storyPoints(item.getStoryPoints())

            .assignee(
                    workItemMapper.toUserSummary(
                            item.getAssignee()
                    )
            )

            .build();
}

    private WorkItem findWorkItem(
            UUID workItemId
    ) {

        return workItemRepository.findById(workItemId)

                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Work item not found: "
                                        + workItemId
                        )
                );
    }

    private WorkItem findWorkItemWithDetails(
            UUID workItemId
    ) {

        return workItemRepository
                .findByIdWithDetails(workItemId)

                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Work item not found: "
                                        + workItemId
                        )
                );
    }

    private Project findProject(
            UUID projectId
    ) {

        return projectRepository.findById(projectId)

                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Project not found: "
                                        + projectId
                        )
                );
    }

    private Sprint findSprint(
            UUID sprintId
    ) {

        return sprintRepository.findById(sprintId)

                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Sprint not found: "
                                        + sprintId
                        )
                );
    }

    private User findUser(
            UUID userId
    ) {

        return userRepository.findById(userId)

                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "User not found: "
                                        + userId
                        )
                );
    }

    private User getUserIfPresent(
            UUID userId
    ) {

        if (userId == null) {
            return null;
        }

        return findUser(userId);
    }

    private Sprint getSprintIfPresent(
            UUID sprintId
    ) {

        if (sprintId == null) {
            return null;
        }

        return findSprint(sprintId);
    }

    private void validateSprintProject(
            Project project,
            Sprint sprint
    ) {

        if (sprint == null) {
            return;
        }

        if (!project.getId().equals(
                sprint.getProject().getId()
        )) {

            throw new IllegalStateException(
                    "Sprint belongs to another project."
            );
        }
    }

    private void validateSprintAssignment(
            WorkItem workItem,
            Sprint sprint
    ) {

        validateSprintProject(
                workItem.getProject(),
                sprint
        );

        if (sprint.getStatus() ==
                Sprint.SprintStatus.COMPLETED) {

            throw new IllegalStateException(
                    "Cannot assign work item to completed sprint."
            );
        }
    }

    private String generateItemKey(
            Project project
    ) {

        long count =
                workItemRepository.count() + 1;

        return project.getKey()
                + "-"
                + count;
    }
}