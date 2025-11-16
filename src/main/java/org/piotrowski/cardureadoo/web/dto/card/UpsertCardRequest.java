package org.piotrowski.cardureadoo.web.dto.card;

import jakarta.validation.constraints.NotBlank;

public record UpsertCardRequest(
        @NotBlank String expExternalId,
        @NotBlank String cardNumber,
        @NotBlank String cardName,
        @NotBlank String cardRarity
) { }
