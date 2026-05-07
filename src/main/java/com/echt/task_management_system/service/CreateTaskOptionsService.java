package com.echt.task_management_system.service;

import com.echt.task_management_system.dto.CreateTaskOptionsResponse;
import com.echt.task_management_system.entity.WorkItem;
import com.echt.task_management_system.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateTaskOptionsService {

    private final ProjectRepository projectRepository;

    public CreateTaskOptionsResponse getOptions() {
        List<CreateTaskOptionsResponse.SpaceOption> spaces = projectRepository.findAllByOrderByNameAsc()
                .stream()
                .map(project -> new CreateTaskOptionsResponse.SpaceOption(project.getId(), project.getName()))
                .toList();

        List<CreateTaskOptionsResponse.EnumOption> workTypes = List.of(
                new CreateTaskOptionsResponse.EnumOption(WorkItem.WorkType.EPIC.name(), "Epic"),
                new CreateTaskOptionsResponse.EnumOption(WorkItem.WorkType.STORY.name(), "Story"),
                new CreateTaskOptionsResponse.EnumOption(WorkItem.WorkType.TASK.name(), "Task"),
                new CreateTaskOptionsResponse.EnumOption(WorkItem.WorkType.BUG.name(), "Bug")
        );

        List<CreateTaskOptionsResponse.EnumOption> statuses = List.of(
                new CreateTaskOptionsResponse.EnumOption(WorkItem.WorkItemStatus.TO_DO.name(), "Todo"),
                new CreateTaskOptionsResponse.EnumOption(WorkItem.WorkItemStatus.IN_PROGRESS.name(), "In Progress"),
                new CreateTaskOptionsResponse.EnumOption(WorkItem.WorkItemStatus.IN_REVIEW.name(), "In Review"),
                new CreateTaskOptionsResponse.EnumOption(WorkItem.WorkItemStatus.DONE.name(), "Done")
        );

        return new CreateTaskOptionsResponse(spaces, workTypes, statuses);
    }
}
