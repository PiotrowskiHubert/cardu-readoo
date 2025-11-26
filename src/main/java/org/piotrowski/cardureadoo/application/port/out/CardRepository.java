package org.piotrowski.cardureadoo.application.port.out;

import org.piotrowski.cardureadoo.domain.model.Card;
import org.piotrowski.cardureadoo.domain.model.value.card.CardName;
import org.piotrowski.cardureadoo.domain.model.value.card.CardNumber;
import org.piotrowski.cardureadoo.domain.model.value.card.CardRarity;
import org.piotrowski.cardureadoo.domain.model.value.expansion.ExpansionExternalId;

import java.util.List;
import java.util.Optional;

public interface CardRepository {

    Card save(Card card);
    Optional<Card> findById(Long id);
//    Optional<Card> find(ExpansionExternalId expId, CardNumber number);
    boolean exists(ExpansionExternalId expId, CardNumber number);
    List<Card> listByExpansion(ExpansionExternalId expId);
    void deleteById(Long id);
    int deleteByIds(List<Long> ids);
    Optional<Long> findIdByExpansionAndNumber(String expExternalId, String cardNumber);
    List<Long> findIdsByExpansionAndName(String expExternalId, String cardName);
    List<Long> findIdsByExpansion(String expExternalId);
    void patch(ExpansionExternalId expId, CardNumber cardNumber, CardName cardName, CardRarity cardRarity);
}
