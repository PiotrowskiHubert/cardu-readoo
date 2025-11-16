package org.piotrowski.cardureadoo.application.port.in;

import org.piotrowski.cardureadoo.domain.model.Card;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CardService {
    @Transactional
    Card save(UpsertCardCommand cmd);

    @Transactional(readOnly = true)
    Optional<Card> find(String expExternalId, String cardNumber);

    @Transactional(readOnly = true)
    boolean exists(String expExternalId, String cardNumber);

    record UpsertCardCommand(
            String expExternalId, String cardNumber, String cardName, String cardRarity
    ) {}
}
