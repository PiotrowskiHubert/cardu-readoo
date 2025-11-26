package org.piotrowski.cardureadoo.web.rest.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.piotrowski.cardureadoo.application.port.in.OfferService;
import org.piotrowski.cardureadoo.web.dto.offer.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/offers", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
public class OfferController {

    private final OfferService offerService;
    private final OfferDtoMapper mapper;

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addOffer(@Valid @RequestBody CreateOfferRequest req) {
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
    public ResponseEntity<List<OfferPointResponse>> getOffersForCard(
            @RequestParam("expId") @NotBlank String expExternalId,
            @RequestParam("cardName") @NotBlank String cardName,
            @RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
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
    public ResponseEntity<Void> patch(
            @PathVariable long id,
            @RequestBody PatchOfferRequest req
    ) {
        offerService.patch(id, new OfferService.PatchOfferCommand(
                req.amount(), req.currency(), req.listedAt()
        ));

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable long id) {
        offerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
