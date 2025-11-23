package org.piotrowski.cardureadoo.web.rest.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.piotrowski.cardureadoo.application.port.in.OfferService;
import org.piotrowski.cardureadoo.web.dto.offer.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
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

    @GetMapping
    public ResponseEntity<List<OfferPointResponse>> getOffers(
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

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addOffer(@Valid @RequestBody AddOfferRequest req) {
        OfferService.AddOfferCommand cmd = mapper.toCommand(req);

        long newId = offerService.addOffer(cmd);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newId)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PatchMapping(path = "/{id}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> patch(
            @PathVariable long id,
            @RequestBody PatchOfferRequest req
    ) {
        if (req == null || (req.amount() == null && req.currency() == null && req.listedAt() == null)) {
            throw new ResponseStatusException(BAD_REQUEST, "Patch body is empty");
        }

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
