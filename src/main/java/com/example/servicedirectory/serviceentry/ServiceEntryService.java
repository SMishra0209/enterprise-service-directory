package com.example.servicedirectory.serviceentry;

import java.util.List;

import com.example.servicedirectory.common.ResourceNotFoundException;
import com.example.servicedirectory.team.Team;
import com.example.servicedirectory.team.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServiceEntryService {

    private final ServiceEntryRepository serviceEntryRepository;
    private final TeamRepository teamRepository;

    public List<ServiceEntryResponse> findAll() {
        return serviceEntryRepository.findAll().stream().map(ServiceEntryResponse::from).toList();
    }

    public Page<ServiceEntryResponse> search(String name, ServiceStatus status, ServiceEnvironment environment,
                                             Long teamId, Pageable pageable) {
        var spec = ServiceEntrySpecifications.filterBy(name, status, environment, teamId);
        return serviceEntryRepository.findAll(spec, pageable).map(ServiceEntryResponse::from);
    }

    public ServiceEntryResponse findById(Long id) {
        return ServiceEntryResponse.from(getEntry(id));
    }

    @Transactional
    public ServiceEntryResponse create(ServiceEntryRequest request) {
        ServiceEntry entry = new ServiceEntry();
        apply(entry, request);
        return ServiceEntryResponse.from(serviceEntryRepository.saveAndFlush(entry));
    }

    @Transactional
    public ServiceEntryResponse update(Long id, ServiceEntryRequest request) {
        ServiceEntry entry = getEntry(id);
        apply(entry, request);
        // saveAndFlush writes immediately, so updatedAt is already refreshed in the response
        return ServiceEntryResponse.from(serviceEntryRepository.saveAndFlush(entry));
    }

    @Transactional
    public void delete(Long id) {
        serviceEntryRepository.delete(getEntry(id));
    }

    private ServiceEntry getEntry(Long id) {
        return serviceEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found: " + id));
    }

    private void apply(ServiceEntry entry, ServiceEntryRequest request) {
        Team team = teamRepository.findById(request.teamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + request.teamId()));
        entry.setName(request.name().trim());
        entry.setDescription(request.description());
        entry.setStatus(request.status());
        entry.setEnvironment(request.environment());
        entry.setBaseUrl(request.baseUrl());
        entry.setRepoUrl(request.repoUrl());
        entry.setTeam(team);
    }
}