package com.echt.task_management_system.exception;

import com.echt.task_management_system.dto.ApiErrorResponse;
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

        return ResponseEntity.badRequest().body(new ApiErrorResponse("Validation failed", fieldErrors));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse(ex.getMessage(), Map.of()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(new ApiErrorResponse(ex.getMessage(), Map.of()));
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
        if (lower.contains("workType".toLowerCase()) || lower.contains("work type".toLowerCase())) {
            fieldErrors.put("workType", "workType must be one of EPIC, STORY, TASK, BUG");
        }
        if (lower.contains("status".toLowerCase())) {
            fieldErrors.put("status", "status must be one of TO_DO, IN_PROGRESS, IN_REVIEW, DONE");
        }

        // If we couldn't attribute to a specific field, still return a useful error body.
        if (fieldErrors.isEmpty()) {
            fieldErrors = Collections.emptyMap();
        }

        return ResponseEntity.badRequest().body(new ApiErrorResponse("Malformed JSON request", fieldErrors));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse("Conflict", Map.of()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnhandled(Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErrorResponse("Internal server error", Map.of()));
    }
}

