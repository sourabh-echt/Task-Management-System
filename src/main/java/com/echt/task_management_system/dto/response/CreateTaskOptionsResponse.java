package com.echt.task_management_system.dto.response;

import java.util.List;
import java.util.UUID;

public record CreateTaskOptionsResponse(
        List<SpaceOption> spaces,
        List<EnumOption> workTypes,
        List<EnumOption> statuses
) {
    public record SpaceOption(UUID id, String name) {}

    public record EnumOption(String value, String label) {}
}
