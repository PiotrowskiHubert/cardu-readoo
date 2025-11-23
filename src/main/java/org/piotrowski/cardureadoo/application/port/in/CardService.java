package org.piotrowski.cardureadoo.application.port.in;

import org.piotrowski.cardureadoo.domain.model.Card;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CardService {

    @Transactional
    Card save(UpsertCardCommand cmd);

    @Transactional(readOnly = true)
    List<Card> listByExpansion(String expExternalId, int page, int size);

    @Transactional
    void deleteById(Long id);

    @Transactional
    int deleteByExpansionAndNumber(String expExternalId, String cardNumber);

    @Transactional
    void patch(String expExternalId, String cardNumber, PatchCardCommand cmd);

    record UpsertCardCommand(String expExternalId, String cardNumber, String cardName, String cardRarity) {}
    record PatchCardCommand(String cardName, String cardRarity) {}
}
