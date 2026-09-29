package com.example.servicedirectory.common;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 404 — resource not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    // 400 — bean validation failures (@Valid @RequestBody)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ApiError.FieldErrorDetail> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ApiError.FieldErrorDetail(fe.getField(), fe.getDefaultMessage()))
                .collect(Collectors.toList());
        return build(HttpStatus.BAD_REQUEST, "Validation failed for one or more fields.", request, fieldErrors);
    }

    // 400 — malformed JSON body, or an invalid enum value inside it
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String message = "The request body is missing or malformed.";
        Throwable cause = ex.getMostSpecificCause();
        String causeMsg = cause != null ? cause.getMessage() : null;
        if (causeMsg != null && causeMsg.contains("ServiceStatus")) {
            message = "Invalid value for 'status'. Allowed values: ACTIVE, DEPRECATED, RETIRED.";
        } else if (causeMsg != null && causeMsg.contains("ServiceEnvironment")) {
            message = "Invalid value for 'environment'. Allowed values: DEV, STAGING, PROD.";
        }
        return build(HttpStatus.BAD_REQUEST, message, request, null);
    }

    // 400 — wrong type in a path variable, e.g. GET /teams/abc
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "Invalid value for parameter '" + ex.getName() + "'.", request, null);
    }

    // 409 — unique constraint / foreign key violations
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleConflict(DataIntegrityViolationException ex, HttpServletRequest request) {
        String message = "The request could not be completed because it conflicts with existing data.";
        String rootMsg = ex.getMostSpecificCause().getMessage();
        if (rootMsg != null) {
            if (rootMsg.contains("uq_team_name")) {
                message = "A team with this name already exists.";
            } else if (rootMsg.contains("uq_service_entry_name")) {
                message = "A service with this name already exists.";
            } else if (rootMsg.contains("fk_service_entry_team")) {
                message = "This team cannot be deleted because it still owns one or more services.";
            }
        }
        return build(HttpStatus.CONFLICT, message, request, null);
    }

    // 405 — wrong HTTP method, keep the real status instead of falling into 500
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), request, null);
    }

    // fallback — anything else becomes a logged 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.", request, null);
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String message, HttpServletRequest request,
                                           List<ApiError.FieldErrorDetail> fieldErrors) {
        ApiError apiError = new ApiError(
                Instant.now(), status.value(), status.getReasonPhrase(), message, request.getRequestURI(), fieldErrors
        );
        return ResponseEntity.status(status).body(apiError);
    }
}