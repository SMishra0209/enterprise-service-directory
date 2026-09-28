package com.example.servicedirectory.serviceentry;

import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class ServiceEntryController {

    private final ServiceEntryService serviceEntryService;

    @GetMapping
    public List<ServiceEntryResponse> list() {
        return serviceEntryService.findAll();
    }

    @GetMapping("/{id}")
    public ServiceEntryResponse get(@PathVariable Long id) {
        return serviceEntryService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceEntryResponse create(@Valid @RequestBody ServiceEntryRequest request) {
        return serviceEntryService.create(request);
    }

    @PutMapping("/{id}")
    public ServiceEntryResponse update(@PathVariable Long id, @Valid @RequestBody ServiceEntryRequest request) {
        return serviceEntryService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        serviceEntryService.delete(id);
    }
}