package com.echt.task_management_system.mapper;
import com.echt.task_management_system.dto.response.SprintResponse;
import com.echt.task_management_system.dto.response.UserSummaryResponse;
import com.echt.task_management_system.dto.response.WorkItemSummaryResponse;
import com.echt.task_management_system.entity.Project;
import com.echt.task_management_system.entity.Sprint;
import com.echt.task_management_system.entity.User;
import com.echt.task_management_system.entity.WorkItem;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class SprintMapper {

    public SprintResponse toResponse(Sprint sprint) {

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
                sprint.getUpdatedAt()
        );
    }

    public SprintResponse buildBacklogResponse(
            Project project,
            List<WorkItem> workItems
    ) {

        return buildResponse(
                null,
                "Backlog",
                null,
                null,
                null,
                null,
                project,
                workItems,
                null,
                null
        );
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
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {

        List<WorkItem> items =
                workItems == null ? List.of() : workItems;

        return SprintResponse.builder()
                .id(id)
                .name(name)
                .goal(goal)
                .status(status)
                .startDate(startDate)
                .endDate(endDate)
                .projectId(project.getId())
                .projectKey(project.getKey())
                .workItems(items.stream()
                        .map(this::toWorkItemSummary)
                        .toList())
                .totalItems(items.size())
                .doneItems(countByStatus(
                        items,
                        WorkItem.WorkItemStatus.DONE
                ))
                .inProgressItems(countByStatus(
                        items,
                        WorkItem.WorkItemStatus.IN_PROGRESS
                ))
                .toDoItems(countByStatus(
                        items,
                        WorkItem.WorkItemStatus.TO_DO
                ))
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    private int countByStatus(
            List<WorkItem> items,
            WorkItem.WorkItemStatus status
    ) {

        return (int) items.stream()
                .filter(item -> item.getStatus() == status)
                .count();
    }

    private WorkItemSummaryResponse toWorkItemSummary(
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
                .build();
    }
}