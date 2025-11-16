package org.piotrowski.cardureadoo.web.rest.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.piotrowski.cardureadoo.application.port.in.CardService;
import org.piotrowski.cardureadoo.web.dto.card.UpsertCardRequest;
import org.springframework.http.ResponseEntity;
import org.piotrowski.cardureadoo.application.port.in.CardService.UpsertCardCommand;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@Validated
public class CardController {
    private final CardService cardService;

    @PostMapping
    public ResponseEntity<Void> upsert(@Valid @RequestBody UpsertCardRequest req) {
        cardService.save(new UpsertCardCommand(req.expExternalId(), req.cardNumber(), req.cardName(), req.cardRarity()));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> exists(@RequestParam @NotBlank String expId, @RequestParam @NotBlank String cardNumber) {
        return ResponseEntity.ok(cardService.exists(expId, cardNumber));
    }
}
