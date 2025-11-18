package org.piotrowski.cardureadoo.web.rest.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.piotrowski.cardureadoo.application.port.in.ExpansionService;
import org.piotrowski.cardureadoo.web.dto.expansion.PatchExpansionRequest;
import org.piotrowski.cardureadoo.web.dto.expansion.UpsertExpansionRequest;
import org.piotrowski.cardureadoo.application.port.in.ExpansionService.UpsertExpansionCommand;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/expansions", produces = "application/json")
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Integer> deleteById(@PathVariable Long id) {
        int removed = expansionService.deleteById(id);
        return ResponseEntity.ok(removed);
    }

    @DeleteMapping("/by-external/{externalId}")
    public ResponseEntity<Integer> deleteByExternal(@PathVariable String externalId) {
        int removed = expansionService.deleteByExternalId(externalId);
        return ResponseEntity.ok(removed);
    }

    @DeleteMapping("/by-name/{name}")
    public ResponseEntity<Integer> deleteByName(@PathVariable String name) {
        int removed = expansionService.deleteByName(name);
        return ResponseEntity.ok(removed);
    }

    @PatchMapping(path = "/{externalId}", consumes = "application/json")
    public ResponseEntity<Void> patch(@PathVariable String externalId, @RequestBody PatchExpansionRequest req) {
        if (req == null || (req.name() == null)) {
            return ResponseEntity.badRequest().build();
        }
        expansionService.patch(externalId, new ExpansionService.PatchExpansionCommand(req.name()));
        return ResponseEntity.noContent().build();
    }
}
