package com.example.servicedirectory.serviceentry;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class ServiceEntrySpecifications {

    private ServiceEntrySpecifications() {
        // utility class
    }

    public static Specification<ServiceEntry> nameContains(String name) {
        if (!StringUtils.hasText(name)) {
            return null; // ignored by Specification.allOf/and-chaining below
        }
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<ServiceEntry> hasStatus(ServiceStatus status) {
        if (status == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<ServiceEntry> hasEnvironment(ServiceEnvironment environment) {
        if (environment == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("environment"), environment);
    }

    public static Specification<ServiceEntry> hasTeamId(Long teamId) {
        if (teamId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("team").get("id"), teamId);
    }

    public static Specification<ServiceEntry> filterBy(String name, ServiceStatus status,
                                                       ServiceEnvironment environment, Long teamId) {
        return Specification.allOf(
                nameContains(name),
                hasStatus(status),
                hasEnvironment(environment),
                hasTeamId(teamId)
        );
    }
}