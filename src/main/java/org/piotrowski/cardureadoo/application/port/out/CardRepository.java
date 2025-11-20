package org.piotrowski.cardureadoo.application.port.out;

import org.piotrowski.cardureadoo.domain.model.Card;
import org.piotrowski.cardureadoo.domain.model.value.card.CardName;
import org.piotrowski.cardureadoo.domain.model.value.card.CardNumber;
import org.piotrowski.cardureadoo.domain.model.value.card.CardRarity;
import org.piotrowski.cardureadoo.domain.model.value.expansion.ExpansionExternalId;

import java.util.List;
import java.util.Optional;

public interface CardRepository {

    // C/U
    Card save(Card card);

    // R
    Optional<Card> find(ExpansionExternalId expId, CardNumber number);

    boolean exists(ExpansionExternalId expId, CardNumber number);

    List<Card> listAll(int page, int size);

    List<Card> listByExpansion(ExpansionExternalId expId, int page, int size);

    List<Card> searchByName(String query, int page, int size);

    // D
    void deleteById(Long id);

    int deleteByIds(List<Long> ids);

    // zapytania pomocnicze po ID/kluczach biznesowych
    Optional<Long> findIdByExpansionAndNumber(String expExternalId, String cardNumber);

    List<Long> findIdsByExpansionAndName(String expExternalId, String cardName);

    List<Long> findIdsByExpansion(String expExternalId);

    // partial update
    void patch(ExpansionExternalId expId, CardNumber cardNumber, CardName cardName, CardRarity cardRarity);
}
