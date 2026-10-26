package com.example.servicedirectory.common;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldErrorDetail> fieldErrors
) {
    public record FieldErrorDetail(String field, String message) {}
}