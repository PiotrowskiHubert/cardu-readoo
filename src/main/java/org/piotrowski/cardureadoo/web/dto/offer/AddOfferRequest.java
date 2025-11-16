package org.piotrowski.cardureadoo.web.dto.offer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.Instant;

public record AddOfferRequest(
        @NotBlank String expExternalId,
        @NotBlank String cardNumber,
        @NotBlank String amount,
        @Pattern(regexp = "^[A-Z]{3}$", message = "currency must be 3-letter ISO code")
        String currency,
        Instant listedAt,
        String cardName,
        String cardRarity
) { }
