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
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Cards", description = "Operations related to cards")
public class CardController {

    private final CardService cardService;
    private final CardDtoMapper dto;

    @PostMapping(
            value = "/api/expansions/{expExternalId}/cards",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new card in expansion", description = "Creates a card assigned to an expansion with the given external identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Card created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Expansion not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Card already exists", content = @Content)
    })
    public ResponseEntity<Void> createCard(
            @Parameter(description = "External identifier of the expansion", required = true)
            @PathVariable String expExternalId,
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

    @GetMapping("/api/cards")
    @Operation(summary = "Get cards by expansion name", description = "Returns a list of all cards related to the given expansion.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cards returned successfully"),
            @ApiResponse(responseCode = "404", description = "Expansion not found", content = @Content)
    })
    public ResponseEntity<List<CardResponse>> getCardsForExpansion(
            @Parameter(description = "Name of the expansion for which cards are returned", required = true)
            @RequestParam("expansionName") @NotBlank String expansionName) {

        var cards = cardService.getByExpansionName(expansionName)
                .stream()
                .map(dto::toResponse)
                .toList();

        if (cards.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        return ResponseEntity.ok(cards);
    }

    @PatchMapping(
            value = "/api/expansions/{expExternalId}/cards/{cardNumber}",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Partially update a card", description = "Allows changing the name and/or rarity of an existing card.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Card updated successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Card not found", content = @Content)
    })
    public ResponseEntity<Void> patchCard(
            @Parameter(description = "External identifier of the expansion", required = true)
            @PathVariable String expExternalId,
            @Parameter(description = "Card number within the expansion", required = true)
            @PathVariable String cardNumber,
            @Valid @RequestBody PatchCardRequest req) {

        cardService.patch(expExternalId, cardNumber,
                new CardService.PatchCardCommand(req.name(), req.rarity())
        );

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/expansions/{expExternalId}/cards/{cardNumber}")
    @Operation(summary = "Delete a card from expansion", description = "Deletes a card with the specified number from the given expansion.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Card deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Card not found", content = @Content)
    })
    public ResponseEntity<Void> deleteByExpExternalIdAndNumber(
            @Parameter(description = "External identifier of the expansion", required = true)
            @PathVariable String expExternalId,
            @Parameter(description = "Card number within the expansion", required = true)
            @PathVariable String cardNumber
    ) {
        cardService.deleteByExpansionAndNumber(expExternalId, cardNumber);
        return ResponseEntity.noContent().build();
    }

}
