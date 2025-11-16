package org.piotrowski.cardureadoo.web.rest.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.piotrowski.cardureadoo.application.port.in.ExpansionService;
import org.piotrowski.cardureadoo.web.dto.expansion.UpsertExpansionRequest;
import org.piotrowski.cardureadoo.application.port.in.ExpansionService.UpsertExpansionCommand;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/expansions")
@RequiredArgsConstructor
@Validated
public class ExpansionController {
    private final ExpansionService expansionService;

    @PostMapping
    public ResponseEntity<Void> upsert(@Valid @RequestBody UpsertExpansionRequest req) {
        expansionService.upsert(new UpsertExpansionCommand(req.externalId(), req.name()));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> exists(@RequestParam @NotBlank String externalId) {
        return ResponseEntity.ok(expansionService.exists(externalId));
    }
}
