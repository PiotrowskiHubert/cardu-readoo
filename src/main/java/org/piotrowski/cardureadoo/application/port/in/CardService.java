package org.piotrowski.cardureadoo.application.port.in;

import org.piotrowski.cardureadoo.domain.model.Card;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CardService {

    @Transactional
    Card create(CreateCardCommand cmd);

    @Transactional(readOnly = true)
    List<Card> getByExpansionName(String expansionName);

    @Transactional
    void deleteById(Long id);

    @Transactional
    void deleteByExpansionAndNumber(String expExternalId, String cardNumber);

    @Transactional
    void patch(String expExternalId, String cardNumber, PatchCardCommand cmd);

    record CreateCardCommand(
            String expExternalId,
            String cardNumber,
            String cardName,
            String cardRarity
    ) {}
    record PatchCardCommand(
            String cardName,
            String cardRarity
    ) {}
}
