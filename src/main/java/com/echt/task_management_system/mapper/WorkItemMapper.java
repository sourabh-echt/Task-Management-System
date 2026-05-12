package com.echt.task_management_system.mapper;

import com.echt.task_management_system.dto.response.AttachmentResponse;
import com.echt.task_management_system.dto.response.TeamResponse;
import com.echt.task_management_system.dto.response.UserSummaryResponse;
import com.echt.task_management_system.dto.response.WorkItemResponse;
import com.echt.task_management_system.entity.Attachment;
import com.echt.task_management_system.entity.Sprint;
import com.echt.task_management_system.entity.Team;
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

                .parent(toParentSummary(workItem.getParent()))

                .sprint(toSprintSummary(sprint))

                .team(toTeamSummary(workItem.getTeam()))

                .assignee(
                        toUserSummary(workItem.getAssignee())
                )

                .reporter(
                        toUserSummary(workItem.getReporter())
                )

                .storyPoints(workItem.getStoryPoints())
                .labels(workItem.getLabels())
                .startDate(workItem.getStartDate())
                .dueDate(workItem.getDueDate())
                .attachment(workItem.getAttachments().stream()
                        .map(this::toAttachmentResponse)
                        .toList())

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
            .build();
}

    private WorkItemResponse.WorkItemParentResponse toParentSummary(WorkItem parent) {
        if (parent == null) {
            return null;
        }
        return new WorkItemResponse.WorkItemParentResponse(
                parent.getId(),
                parent.getItemKey(),
                parent.getWorkType(),
                parent.getSummary()
        );
    }

    private WorkItemResponse.SprintSummaryResponse toSprintSummary(Sprint sprint) {
        if (sprint == null) {
            return null;
        }
        return new WorkItemResponse.SprintSummaryResponse(sprint.getId(), sprint.getName());
    }

    private TeamResponse toTeamSummary(Team team) {
        if (team == null) {
            return null;
        }
        return TeamResponse.builder()
                .teamId(team.getId())
                .teamName(team.getTeamName())
                .teamMembers(team.getTeamMembers().stream()
                        .map(user -> new TeamResponse.Member(
                                user.getId(),
                                user.getDisplayName() != null && !user.getDisplayName().isBlank()
                                        ? user.getDisplayName()
                                        : user.getUsername(),
                                user.getEmail()
                        ))
                        .toList())
                .build();
    }

    private AttachmentResponse toAttachmentResponse(Attachment attachment) {
        return AttachmentResponse.builder()
                .id(attachment.getId())
                .filename(attachment.getFilename())
                .fileUrl(attachment.getFileUrl())
                .fileSize(attachment.getFileSize())
                .build();
    }
}
