package com.echt.task_management_system.service.Impl;

import com.echt.task_management_system.dto.response.CreateTaskOptionsResponse;
import com.echt.task_management_system.entity.WorkItem;
import com.echt.task_management_system.repository.ProjectRepository;
import com.echt.task_management_system.service.CreateTaskOptionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateTaskOptionsServiceImpl implements CreateTaskOptionsService {

    private final ProjectRepository projectRepository;

    @Override
    @Transactional(readOnly = true)
    public CreateTaskOptionsResponse getOptions() {
        List<CreateTaskOptionsResponse.SpaceOption> spaces = projectRepository.findAllByOrderByNameAsc()
                .stream()
                .map(p -> new CreateTaskOptionsResponse.SpaceOption(p.getId(), p.getName()))
                .toList();

        List<CreateTaskOptionsResponse.EnumOption> workTypes = Arrays.stream(WorkItem.WorkType.values())
                .map(v -> new CreateTaskOptionsResponse.EnumOption(v.name(), toLabel(v.name())))
                .toList();

        List<CreateTaskOptionsResponse.EnumOption> statuses = Arrays.stream(WorkItem.WorkItemStatus.values())
                .map(v -> new CreateTaskOptionsResponse.EnumOption(v.name(), toLabel(v.name())))
                .toList();

        return new CreateTaskOptionsResponse(spaces, workTypes, statuses);
    }

    private String toLabel(String enumName) {
        return enumName.replace('_', ' ');
    }
}