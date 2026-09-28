package com.example.servicedirectory.team;

public record TeamResponse(Long id, String name, String contactEmail) {

    public static TeamResponse from(Team team) {
        return new TeamResponse(team.getId(), team.getName(), team.getContactEmail());
    }
}