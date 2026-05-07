package com.echt.task_management_system.dto;

import java.util.Map;

public record ApiErrorResponse(
        String message,
        Map<String, String> fieldErrors
) {
}
