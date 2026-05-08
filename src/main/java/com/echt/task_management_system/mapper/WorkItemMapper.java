package com.echt.task_management_system.mapper;

import com.echt.task_management_system.dto.response.UserSummaryResponse;
import com.echt.task_management_system.dto.response.WorkItemResponse;
import com.echt.task_management_system.entity.Sprint;
import com.echt.task_management_system.entity.User;
import com.echt.task_management_system.entity.WorkItem;

import org.springframework.stereotype.Component;

@Component
public class WorkItemMapper {

    public WorkItemResponse toResponse(WorkItem workItem) {

        Sprint sprint = workItem.getSprint();

        return WorkItemResponse.builder()
                .id(workItem.getId())
                .itemKey(workItem.getItemKey())
                .workType(workItem.getWorkType())
                .summary(workItem.getSummary())
                .description(workItem.getDescription())
                .status(workItem.getStatus())
                .priority(workItem.getPriority())

                .projectId(workItem.getProject().getId())
                .projectKey(workItem.getProject().getKey())

                .sprintId(
                        sprint != null
                                ? sprint.getId()
                                : null
                )

                .sprintName(
                        sprint != null
                                ? sprint.getName()
                                : null
                )

                .assignee(
                        toUserSummary(workItem.getAssignee())
                )

                .reporter(
                        toUserSummary(workItem.getReporter())
                )

                .storyPoints(workItem.getStoryPoints())
                .dueDate(workItem.getDueDate())

                .createdAt(workItem.getCreatedAt())
                .updatedAt(workItem.getUpdatedAt())

                .build();
    }

   public UserSummaryResponse toUserSummary(User user) {

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