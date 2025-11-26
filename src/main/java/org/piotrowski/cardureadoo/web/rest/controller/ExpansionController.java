package org.piotrowski.cardureadoo.web.rest.controller;

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
public class ExpansionController {

    private final ExpansionService expansionService;

    @GetMapping
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

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
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

    @PatchMapping(path = "/{externalId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> patch(@PathVariable @NotBlank String externalId,
                                      @RequestBody PatchExpansionRequest req) {

        expansionService.patch(
                externalId,
                new ExpansionService.PatchExpansionCommand(req.name())
        );

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteByName(@PathVariable String name) {
        expansionService.deleteByName(name);
        return ResponseEntity.noContent().build();
    }
}
