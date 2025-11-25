package org.piotrowski.cardureadoo.web.rest.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.piotrowski.cardureadoo.application.port.in.CardService;
import org.piotrowski.cardureadoo.web.dto.card.CardDtoMapper;
import org.piotrowski.cardureadoo.web.dto.card.CardResponse;
import org.piotrowski.cardureadoo.web.dto.card.PatchCardRequest;
import org.piotrowski.cardureadoo.web.dto.card.CreateCardRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class CardController {

    private final CardService cardService;
    private final CardDtoMapper dto;

    @GetMapping("/api/cards")
    public ResponseEntity<List<CardResponse>> getCardsForExpansion(
            @RequestParam("expansionName") @NotBlank String expansionName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        var cards = cardService.getByExpansionName(expansionName, page, size)
                .stream()
                .map(dto::toResponse)
                .toList();

//        if (cards.isEmpty()) {
//            return ResponseEntity.noContent().build();
//        }

        return ResponseEntity.ok(cards);
    }

    @PostMapping(
            value = "/api/expansions/{expExternalId}/cards",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createCard(
            @PathVariable @NotBlank String expExternalId,
            @Valid @RequestBody CreateCardRequest req) {

        cardService.create(new CardService.CreateCardCommand(
                expExternalId,
                req.cardNumber(),
                req.cardName(),
                req.cardRarity()
        ));

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{cardNumber}")
                .buildAndExpand(req.cardNumber())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PatchMapping(
            value = "/api/expansions/{expExternalId}/cards/{cardNumber}",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> patchCard(
            @PathVariable @NotBlank String expExternalId,
            @PathVariable @NotBlank String cardNumber,
            @RequestBody PatchCardRequest req) {

        cardService.patch(expExternalId, cardNumber,
                new CardService.PatchCardCommand(req.name(), req.rarity())
        );

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/expansions/{expExternalId}/cards/{cardNumber}")
    public ResponseEntity<Void> deleteCard(
            @PathVariable @NotBlank String expExternalId,
            @PathVariable @NotBlank String cardNumber
    ) {
        cardService.deleteByExpansionAndNumber(expExternalId, cardNumber);
        return ResponseEntity.noContent().build();
    }

}
