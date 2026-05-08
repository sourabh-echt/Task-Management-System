package com.echt.task_management_system.common.response;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public record ApiErrorResponse(
        boolean success,
        String message,
        ApiErrorDetail error,
        Instant timestamp
) {
    public static ApiErrorResponse failure(String message, ErrorCode errorCode, Object details) {
        return new ApiErrorResponse(false, message, new ApiErrorDetail(errorCode.name(), details),
                Instant.now().truncatedTo(ChronoUnit.SECONDS));
    }
}
