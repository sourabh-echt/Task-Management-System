package com.echt.task_management_system.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

/**
 * Standard envelope for all API responses.
 * <pre>
 * {
 *   "success": true,
 *   "message": "Sprint created successfully",
 *   "data": { ... },
 *   "timestamp": "2024-05-04T10:30:00Z"
 * }
 * </pre>
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    @Builder.Default
    private OffsetDateTime timestamp = OffsetDateTime.now();

    // ── Static factories ─────────────────────────────────────────────────────

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder().success(true).data(data).build();
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return ApiResponse.<T>builder().success(true).message(message).data(data).build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder().success(false).message(message).build();
    }
}
