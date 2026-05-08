package com.echt.task_management_system.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BoardResponse {

    private List<WorkItemSummaryResponse> toDo;

    private List<WorkItemSummaryResponse> inProgress;

    private List<WorkItemSummaryResponse> inReview;

    private List<WorkItemSummaryResponse> done;
}