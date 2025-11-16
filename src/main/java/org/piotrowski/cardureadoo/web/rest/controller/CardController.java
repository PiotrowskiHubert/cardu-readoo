package org.piotrowski.cardureadoo.web.rest.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.piotrowski.cardureadoo.application.port.in.CardService;
import org.piotrowski.cardureadoo.web.dto.card.CardDtoMapper;
import org.piotrowski.cardureadoo.web.dto.card.CardResponse;
import org.piotrowski.cardureadoo.web.dto.card.UpsertCardRequest;
import org.springframework.http.ResponseEntity;
import org.piotrowski.cardureadoo.application.port.in.CardService.UpsertCardCommand;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@Validated
public class CardController {

    private final CardService cardService;
    private final CardDtoMapper dto;

    @GetMapping
    public List<CardResponse> getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "50") int size) {
        return cardService.listAll(page, size).stream()
                .map(dto::toResponse)
                .toList();
    }

    @GetMapping("/by-expansion/{expExternalId}")
    public List<CardResponse> getByExpansion(@PathVariable String expExternalId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "50") int size) {
        return cardService.listByExpansion(expExternalId, page, size).stream()
                .map(dto::toResponse)
                .toList();
    }

    @GetMapping("/by-number")
    public ResponseEntity<CardResponse> getByNumber(@RequestParam String expExternalId, @RequestParam String cardNumber) {
        return cardService.find(expExternalId, cardNumber)
                .map(dto::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<CardResponse> searchByName(@RequestParam String name, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "50") int size) {
        return cardService.searchByName(name, page, size).stream()
                .map(dto::toResponse)
                .toList();
    }

    @PostMapping
    public ResponseEntity<Void> upsert(@Valid @RequestBody UpsertCardRequest req) {
        cardService.save(new UpsertCardCommand(req.expExternalId(), req.cardNumber(), req.cardName(), req.cardRarity()));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> exists(@RequestParam @NotBlank String expId, @RequestParam @NotBlank String cardNumber) {
        return ResponseEntity.ok(cardService.exists(expId, cardNumber));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        cardService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/by-number")
    public ResponseEntity<Integer> deleteByNumber(
            @RequestParam("expansion") String expExternalId,
            @RequestParam("number") String cardNumber) {
        int removed = cardService.deleteByExpansionAndNumber(expExternalId, cardNumber);
        return ResponseEntity.ok(removed);
    }

    @DeleteMapping("/by-name")
    public ResponseEntity<Integer> deleteByName(
            @RequestParam("expansion") String expExternalId,
            @RequestParam("name") String cardName) {
        int removed = cardService.deleteByExpansionAndName(expExternalId, cardName);
        return ResponseEntity.ok(removed);
    }
}
