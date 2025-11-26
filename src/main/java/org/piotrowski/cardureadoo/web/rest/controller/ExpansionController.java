package org.piotrowski.cardureadoo.web.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.piotrowski.cardureadoo.application.port.in.ExpansionService;
import org.piotrowski.cardureadoo.web.dto.expansion.ExpansionResponse;
import org.piotrowski.cardureadoo.web.dto.expansion.PatchExpansionRequest;
import org.piotrowski.cardureadoo.web.dto.expansion.CreateExpansionRequest;
import org.piotrowski.cardureadoo.application.port.in.ExpansionService.CreateExpansionCommand;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value = "/api/expansions", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
@Tag(name = "Expansions", description = "Operations related to expansions")
public class ExpansionController {

    private final ExpansionService expansionService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new expansion", description = "Creates a new expansion using the provided data.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Expansion created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "409", description = "Expansion already exists", content = @Content)
    })
    public ResponseEntity<Void> create(@Valid @RequestBody CreateExpansionRequest req) {
        var created = expansionService.create(
                new CreateExpansionCommand(req.externalId(), req.name())
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{name}")
                .buildAndExpand(created.getName().value())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping
    @Operation(summary = "Get all expansions", description = "Returns a list of all available expansions.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Expansions returned successfully")
    })
    public ResponseEntity<List<ExpansionResponse>> getAll() {
        var exps = expansionService.findAll();

        var dtos = exps.stream()
                .map(e -> new ExpansionResponse(e.getId().value(), e.getName().value()))
                .toList();

        if (dtos.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        return ResponseEntity.ok(dtos);
    }

    @PatchMapping(path = "/{externalId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update an expansion", description = "Partially updates expansion data for the given external identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Expansion updated successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Expansion not found", content = @Content)
    })
    public ResponseEntity<Void> patch(
            @Parameter(description = "External identifier of the expansion", required = true)
            @PathVariable String externalId,
            @Valid @RequestBody PatchExpansionRequest req) {

        expansionService.patch(
                externalId,
                new ExpansionService.PatchExpansionCommand(req.name())
        );

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{name}")
    @Operation(summary = "Delete an expansion", description = "Deletes an expansion with the given name.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Expansion deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Expansion not found", content = @Content)
    })
    public ResponseEntity<Void> deleteByName(
            @Parameter(description = "Name of the expansion", required = true)
            @PathVariable String name) {
        expansionService.deleteByName(name);
        return ResponseEntity.noContent().build();
    }
}
