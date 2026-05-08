package com.echt.task_management_system.exception;

import com.echt.task_management_system.common.response.ApiErrorResponse;
import com.echt.task_management_system.common.response.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();

        ex.getFieldErrors().forEach(err -> fieldErrors.put(err.getField(), err.getDefaultMessage()));

        return error(HttpStatus.BAD_REQUEST, "Validation failed", ErrorCode.VALIDATION_FAILED, fieldErrors);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(EntityNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage(), ErrorCode.RESOURCE_NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage(), ErrorCode.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleBadJson(HttpMessageNotReadableException ex) {
        // Common case: spaceId isn't a valid UUID, or enum can't be mapped.
        String msg = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();

        Map<String, String> fieldErrors = new HashMap<>();
        String lower = msg != null ? msg.toLowerCase() : "";
        if (lower.contains("spaceId".toLowerCase()) || lower.contains("space id".toLowerCase())) {
            fieldErrors.put("spaceId", "spaceId must be a valid UUID");
        }
        if (lower.contains("assigneeId".toLowerCase()) || lower.contains("assignee id".toLowerCase())) {
            fieldErrors.put("assigneeId", "assigneeId must be a valid UUID");
        }
        if (lower.contains("reporterId".toLowerCase()) || lower.contains("reporter id".toLowerCase())) {
            fieldErrors.put("reporterId", "reporterId must be a valid UUID");
        }
        if (lower.contains("workType".toLowerCase()) || lower.contains("work type".toLowerCase())) {
            fieldErrors.put("workType", "workType must be one of EPIC, STORY, TASK, BUG");
        }
        if (lower.contains("status".toLowerCase())) {
            fieldErrors.put("status", "status must be one of TO_DO, TODO, IN_PROGRESS, IN_REVIEW, DONE");
        }
        if (lower.contains("priority".toLowerCase())) {
            fieldErrors.put("priority", "priority must be one of LOWEST, LOW, MEDIUM, HIGH, HIGHEST");
        }

        // If we couldn't attribute to a specific field, still return a useful error body.
        if (fieldErrors.isEmpty()) {
            fieldErrors = Collections.emptyMap();
        }

        Object details = fieldErrors.isEmpty() ? "Request body is invalid or unreadable" : fieldErrors;
        return error(HttpStatus.BAD_REQUEST, "Malformed JSON request", ErrorCode.MALFORMED_JSON, details);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(DataIntegrityViolationException ex) {
        return error(HttpStatus.CONFLICT, "Conflict", ErrorCode.DATA_INTEGRITY_VIOLATION,
                "Request conflicts with existing data");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnhandled(Exception ex) {
        log.error("Unhandled exception", ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", ErrorCode.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred");
    }

    private ResponseEntity<ApiErrorResponse> error(HttpStatus status, String message, ErrorCode errorCode, Object details) {
        return ResponseEntity.status(status)
                .body(ApiErrorResponse.failure(message, errorCode, details));
    }
}
