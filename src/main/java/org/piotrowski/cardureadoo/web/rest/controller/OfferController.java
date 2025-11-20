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

import java.net.URI;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping(value = "/api/offers", produces = "application/json")
@RequiredArgsConstructor
@Validated
public class OfferController {

    private final OfferService offerService;
    private final OfferDtoMapper mapper;

    // POST
    @PostMapping
    public ResponseEntity<Void> addOffer(@Valid @RequestBody AddOfferRequest req) {
        OfferService.AddOfferCommand cmd = mapper.toCommand(req);
        offerService.addOffer(cmd);

        return ResponseEntity.created(URI.create("/api/offers")).build();
    }

    // GETs
    @GetMapping
    public ResponseEntity<List<OfferPointResponse>> getOffers(
            @RequestParam("expId") @NotBlank String expExternalId,
            @RequestParam("cardNumber") @NotBlank String cardNumber,
            @RequestParam(value = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(value = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to
    ) {
        var points = offerService.getOffers(expExternalId, cardNumber, from, to)
                .stream()
                .map(mapper::toResponse)
                .toList();

        return ResponseEntity.ok(points);
    }

    @GetMapping("/all")
    public ResponseEntity<List<OfferPointResponse>> getAllOffers(
            @RequestParam(value = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(value = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to
    ) {
        var points = offerService.getAll(from, to).stream()
                .map(mapper::toResponse)
                .toList();

        return ResponseEntity.ok(points);
    }

    @GetMapping("/by-card-name")
    public ResponseEntity<List<OfferPointResponse>> getOffersByCardName(
            @RequestParam("expId") @NotBlank String expExternalId,
            @RequestParam("cardName") @NotBlank String cardName,
            @RequestParam(value = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(value = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to
    ) {
        var points = offerService.getOffersByCardName(expExternalId, cardName, from, to)
                .stream()
                .map(mapper::toResponse)
                .toList();

        return ResponseEntity.ok(points);
    }

    @GetMapping("/last")
    public ResponseEntity<OfferPointResponse> getLast(
            @RequestParam("expId") @NotBlank String expExternalId,
            @RequestParam("cardNumber") @NotBlank String cardNumber
    ) {
        var last = offerService.getLast(expExternalId, cardNumber);
        return last == null ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(mapper.toResponse(last));
    }

    @GetMapping("/stats")
    public ResponseEntity<OfferStatsResponse> getStats(
            @RequestParam("expId") @NotBlank String expExternalId,
            @RequestParam("cardNumber") @NotBlank String cardNumber,
            @RequestParam(value = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(value = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to
    ) {
        var stats = offerService.getStats(expExternalId, cardNumber, from, to);
        return ResponseEntity.ok(mapper.toResponse(stats));
    }

    // PATCH
    @PatchMapping(path = "/{id}", consumes = "application/json")
    public ResponseEntity<Void> patch(@PathVariable long id,
                                      @RequestBody PatchOfferRequest req) {
        if (req == null || (req.amount() == null && req.currency() == null && req.listedAt() == null)) {
            return ResponseEntity.badRequest().build();
        }
        offerService.patch(id, new OfferService.PatchOfferCommand(
                req.amount(), req.currency(), req.listedAt()
        ));
        return ResponseEntity.noContent().build();
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        offerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
