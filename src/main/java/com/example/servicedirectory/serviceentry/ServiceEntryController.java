package com.example.servicedirectory.serviceentry;

import java.util.List;

import com.example.servicedirectory.common.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Get a service by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service found"),
            @ApiResponse(responseCode = "404", description = "Service not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    public ServiceEntryResponse get(@PathVariable Long id) {
        return serviceEntryService.findById(id);
    }

    @Operation(summary = "Create a service")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Service created"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Referenced team not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "A service with this name already exists",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceEntryResponse create(@Valid @RequestBody ServiceEntryRequest request) {
        return serviceEntryService.create(request);
    }

    @Operation(summary = "Update a service")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Service or referenced team not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "A service with this name already exists",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}")
    public ServiceEntryResponse update(@PathVariable Long id, @Valid @RequestBody ServiceEntryRequest request) {
        return serviceEntryService.update(id, request);
    }

    @Operation(summary = "Delete a service")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Service deleted"),
            @ApiResponse(responseCode = "404", description = "Service not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        serviceEntryService.delete(id);
    }
}