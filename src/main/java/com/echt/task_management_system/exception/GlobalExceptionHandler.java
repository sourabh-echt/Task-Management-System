package com.echt.task_management_system.exception;

import com.echt.task_management_system.common.response.ApiErrorResponse;
import com.echt.task_management_system.common.response.ErrorCode;

import jakarta.persistence.EntityNotFoundException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Bean validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex
    ) {

        Map<String, String> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {

            errors.put(
                    error.getField(),
                    error.getDefaultMessage()
            );
        }

        return error(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                ErrorCode.VALIDATION_FAILED,
                errors
        );
    }

    /**
     * Entity not found
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleEntityNotFound(
            EntityNotFoundException ex
    ) {

        return error(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                ErrorCode.RESOURCE_NOT_FOUND,
                ex.getMessage()
        );
    }

    /**
     * Bad request / illegal argument
     */
    @ExceptionHandler({
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    public ResponseEntity<ApiErrorResponse> handleIllegalState(
            RuntimeException ex
    ) {

        return error(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                ErrorCode.BAD_REQUEST,
                ex.getMessage()
        );
    }

    /**
     * Invalid login credentials
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(
            BadCredentialsException ex
    ) {

        return error(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password",
                ErrorCode.AUTHENTICATION_FAILED,
                "Invalid email or password"
        );
    }

    /**
     * Invalid JSON / enum / UUID parsing errors
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleBadJson(
            HttpMessageNotReadableException ex
    ) {

        String msg = ex.getMostSpecificCause() != null
                ? ex.getMostSpecificCause().getMessage()
                : ex.getMessage();

        Map<String, String> fieldErrors = new HashMap<>();

        String lower = msg != null
                ? msg.toLowerCase()
                : "";

        if (lower.contains("spaceid")
                || lower.contains("space id")) {

            fieldErrors.put(
                    "spaceId",
                    "spaceId must be a valid UUID"
            );
        }

        if (lower.contains("assigneeid")
                || lower.contains("assignee id")) {

            fieldErrors.put(
                    "assigneeId",
                    "assigneeId must be a valid UUID"
            );
        }

        if (lower.contains("reporterid")
                || lower.contains("reporter id")) {

            fieldErrors.put(
                    "reporterId",
                    "reporterId must be a valid UUID"
            );
        }

        if (lower.contains("sprintid")
                || lower.contains("sprint id")) {

            fieldErrors.put(
                    "sprintId",
                    "sprintId must be a valid UUID"
            );
        }

        if (lower.contains("worktype")
                || lower.contains("work type")) {

            fieldErrors.put(
                    "workType",
                    "workType must be one of EPIC, STORY, TASK, BUG"
            );
        }

        if (lower.contains("status")) {

            fieldErrors.put(
                    "status",
                    "status must be one of TO_DO, TODO, IN_PROGRESS, IN_REVIEW, DONE"
            );
        }

        if (lower.contains("priority")) {

            fieldErrors.put(
                    "priority",
                    "priority must be one of LOWEST, LOW, MEDIUM, HIGH, HIGHEST"
            );
        }

        if (lower.contains("role")) {

            fieldErrors.put(
                    "role",
                    "role must be one of ADMIN, PROJECT_MANAGER, DEVELOPER, TESTER, VIEWER"
            );
        }

        if (fieldErrors.isEmpty()) {
            fieldErrors = Collections.emptyMap();
        }

        Object details = fieldErrors.isEmpty()
                ? "Request body is invalid or unreadable"
                : fieldErrors;

        return error(
                HttpStatus.BAD_REQUEST,
                "Malformed JSON request",
                ErrorCode.MALFORMED_JSON,
                details
        );
    }

    /**
     * Database conflicts / constraint violations
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(
            DataIntegrityViolationException ex
    ) {

        return error(
                HttpStatus.CONFLICT,
                "Conflict",
                ErrorCode.DATA_INTEGRITY_VIOLATION,
                "Request conflicts with existing data"
        );
    }

    /**
     * Fallback handler
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneralException(
            Exception ex
    ) {

        log.error("Unhandled exception", ex);

        return error(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error",
                ErrorCode.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred"
        );
    }

    /**
     * Common error response builder
     */
    private ResponseEntity<ApiErrorResponse> error(
            HttpStatus status,
            String message,
            ErrorCode errorCode,
            Object details
    ) {

        return ResponseEntity.status(status)
                .body(
                        ApiErrorResponse.failure(
                                message,
                                errorCode,
                                details
                        )
                );
    }
}
