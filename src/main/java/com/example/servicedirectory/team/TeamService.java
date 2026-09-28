package com.example.servicedirectory.team;

import java.util.List;

import com.example.servicedirectory.common.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository teamRepository;

    public List<TeamResponse> findAll() {
        return teamRepository.findAll().stream().map(TeamResponse::from).toList();
    }

    public TeamResponse findById(Long id) {
        return TeamResponse.from(getTeam(id));
    }

    @Transactional
    public TeamResponse create(TeamRequest request) {
        Team team = new Team();
        apply(team, request);
        return TeamResponse.from(teamRepository.save(team));
    }

    @Transactional
    public TeamResponse update(Long id, TeamRequest request) {
        Team team = getTeam(id);
        apply(team, request);
        return TeamResponse.from(team);
    }

    @Transactional
    public void delete(Long id) {
        teamRepository.delete(getTeam(id));
    }

    private Team getTeam(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + id));
    }

    private void apply(Team team, TeamRequest request) {
        team.setName(request.name().trim());
        team.setContactEmail(request.contactEmail().trim());
    }
}