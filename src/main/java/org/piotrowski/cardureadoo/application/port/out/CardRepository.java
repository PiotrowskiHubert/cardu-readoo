package org.piotrowski.cardureadoo.application.port.out;

import org.piotrowski.cardureadoo.domain.model.Card;
import org.piotrowski.cardureadoo.domain.model.value.card.CardNumber;
import org.piotrowski.cardureadoo.domain.model.value.expansion.ExpansionExternalId;

import java.util.Optional;

public interface CardRepository {
    Optional<Card> find(ExpansionExternalId expId, CardNumber number);
    boolean exists(ExpansionExternalId expId, CardNumber number);
    Card save(Card card);
}
