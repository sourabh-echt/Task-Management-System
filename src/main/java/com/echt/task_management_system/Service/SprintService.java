package com.echt.task_management_system.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.echt.task_management_system.dto.request.CreateSprintRequest;
import com.echt.task_management_system.dto.request.UpdateSprintRequest;
import com.echt.task_management_system.dto.response.SprintResponse;

public interface SprintService {
    CreateSprintRequest createSprint(CreateSprintRequest request);
    SprintResponse getSprintById(UUID sprintId);
    List<SprintResponse> getSprintByProjectId(UUID projectId);
    SprintResponse updateSprint(UUID sprintId, UpdateSprintRequest request);

    SprintResponse startSprint(UUID sprintId, LocalDate startDate, LocalDate endDate);

    SprintResponse completeSprint(UUID sprintId);

    void deleteSprint(UUID sprintId);

    SprintResponse getBacklog(UUID projectId);
}
