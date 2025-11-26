package org.piotrowski.cardureadoo.web.dto.offer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.Instant;

public record CreateOfferRequest(
        @NotBlank String expExternalId,
        @NotBlank String cardNumber,
        @NotBlank
        @Pattern(regexp = "^[1-9][0-9]*$", message = "amount must be a positive integer greater than 0")
        String amount,
        @Pattern(regexp = "^[A-Z]{3}$", message = "currency must be 3-letter ISO code")
        String currency,
        Instant listedAt,
        @NotBlank String cardName,
        @NotBlank String cardRarity
) { }
