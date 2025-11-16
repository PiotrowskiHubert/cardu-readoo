package org.piotrowski.cardureadoo.application.port.in;

import org.piotrowski.cardureadoo.application.service.CardApplicationService;
import org.piotrowski.cardureadoo.domain.model.Card;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CardService {
    @Transactional
    Card save(CardApplicationService.UpsertCardCommand cmd);

    @Transactional(readOnly = true)
    Optional<Card> find(String expExternalId, String cardNumber);

    @Transactional(readOnly = true)
    boolean exists(String expExternalId, String cardNumber);
}
