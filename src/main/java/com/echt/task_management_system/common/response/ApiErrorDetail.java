package com.echt.task_management_system.common.response;

public record ApiErrorDetail(
        String errorCode,
        Object details
) {
}
