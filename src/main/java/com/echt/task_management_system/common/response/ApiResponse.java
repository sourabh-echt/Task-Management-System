package com.echt.task_management_system.common.response;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public record ApiResponse<T>(
        boolean success,
        int status,
        String message,
        T data,
        Instant timestamp
) {
    public static <T> ApiResponse<T> success(int status, String message, T data) {
        return new ApiResponse<>(true, status, message, data, Instant.now().truncatedTo(ChronoUnit.SECONDS));
    }
}
