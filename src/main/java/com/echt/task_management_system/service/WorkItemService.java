package com.echt.task_management_system.service;

import com.echt.task_management_system.dto.request.CreateWorkItemRequest;
import com.echt.task_management_system.dto.request.UpdateWorkItemRequest;
import com.echt.task_management_system.dto.response.BoardResponse;
import com.echt.task_management_system.dto.response.WorkItemResponse;

import java.util.UUID;

public interface WorkItemService {

    WorkItemResponse createWorkItem(
            UUID projectId,
            CreateWorkItemRequest request
    );

    WorkItemResponse getWorkItemById(
            UUID workItemId
    );

    WorkItemResponse updateWorkItem(
            UUID workItemId,
            UpdateWorkItemRequest request
    );

    void deleteWorkItem(
            UUID workItemId
    );

    WorkItemResponse assignToSprint(
            UUID workItemId,
            UUID sprintId
    );

    WorkItemResponse moveToBacklog(
            UUID workItemId
    );

    BoardResponse getBoard(
            UUID projectId
    );
}