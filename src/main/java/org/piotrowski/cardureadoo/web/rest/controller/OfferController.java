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
import org.piotrowski.cardureadoo.application.port.in.OfferService;
import org.piotrowski.cardureadoo.web.dto.offer.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/offers", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
@Tag(name = "Offers", description = "Operations related to card offers")
public class OfferController {

    private final OfferService offerService;
    private final OfferDtoMapper mapper;

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Create an offer", description = "Creates a new offer to sell a card.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Offer created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or amount not numeric", content = @Content),
            @ApiResponse(responseCode = "404", description = "Related card or expansion not found", content = @Content)
    })
    public ResponseEntity<Void> createOffer(@Valid @RequestBody CreateOfferRequest req) {
        long newId = offerService.create(new OfferService.CreateOfferCommand(
                req.expExternalId(),
                req.cardNumber(),
                req.amount(),
                req.currency(),
                req.listedAt(),
                req.cardName(),
                req.cardRarity()
        ));

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newId)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping
    @Operation(summary = "Get offers for a card", description = "Returns a list of offer data points for a given card within an optional time range.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Offers returned successfully")
    })
    public ResponseEntity<List<OfferPointResponse>> getOffersForCard(
            @Parameter(description = "External identifier of the expansion", required = true)
            @RequestParam("expId") @NotBlank String expExternalId,
            @Parameter(description = "Name of the card", required = true)
            @RequestParam("cardName") @NotBlank String cardName,
            @Parameter(description = "Start of the time range (optional)")
            @RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @Parameter(description = "End of the time range (optional)")
            @RequestParam(value = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {

        var offers = offerService.getOffersByCardName(expExternalId, cardName, from, to)
                .stream()
                .map(mapper::toResponse)
                .toList();

        if (offers.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        return ResponseEntity.ok(offers);
    }

    @PatchMapping(path = "/{id}", consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Update an offer", description = "Partially updates an existing offer.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Offer updated successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Offer not found", content = @Content)
    })
    public ResponseEntity<Void> patch(
            @Parameter(description = "Identifier of the offer", required = true)
            @PathVariable long id,
            @Valid @RequestBody PatchOfferRequest req
    ) {
        offerService.patch(id, new OfferService.PatchOfferCommand(
                req.amount(), req.currency(), req.listedAt()
        ));

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an offer", description = "Deletes an offer with the given identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Offer deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Offer not found", content = @Content)
    })
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "Identifier of the offer", required = true)
            @NotBlank @PathVariable long id) {
        offerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
