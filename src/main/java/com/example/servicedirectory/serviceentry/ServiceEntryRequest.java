package com.example.servicedirectory.serviceentry;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record ServiceEntryRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 1000) String description,
        @NotNull ServiceStatus status,
        @NotNull ServiceEnvironment environment,
        @Size(max = 500) @URL String baseUrl,
        @Size(max = 500) @URL String repoUrl,
        @NotNull Long teamId
) {
}