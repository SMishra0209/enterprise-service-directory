package com.example.servicedirectory.serviceentry;

import java.time.Instant;

public record ServiceEntryResponse(
        Long id,
        String name,
        String description,
        ServiceStatus status,
        ServiceEnvironment environment,
        String baseUrl,
        String repoUrl,
        Long teamId,
        String teamName,
        Instant createdAt,
        Instant updatedAt
) {
    public static ServiceEntryResponse from(ServiceEntry e) {
        return new ServiceEntryResponse(
                e.getId(), e.getName(), e.getDescription(), e.getStatus(), e.getEnvironment(),
                e.getBaseUrl(), e.getRepoUrl(),
                e.getTeam().getId(), e.getTeam().getName(),
                e.getCreatedAt(), e.getUpdatedAt());
    }
}