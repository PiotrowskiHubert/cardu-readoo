package org.piotrowski.cardureadoo.web.dto.card;

public record CardResponse(
        String expExternalId,
        String cardNumber,
        String cardName,
        String cardRarity
) { }
