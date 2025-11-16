package org.piotrowski.cardureadoo.application.service;

import lombok.RequiredArgsConstructor;
import org.piotrowski.cardureadoo.application.port.in.CardService;
import org.piotrowski.cardureadoo.application.port.out.CardRepository;
import org.piotrowski.cardureadoo.domain.model.Card;
import org.piotrowski.cardureadoo.domain.model.value.card.CardName;
import org.piotrowski.cardureadoo.domain.model.value.card.CardNumber;
import org.piotrowski.cardureadoo.domain.model.value.card.CardRarity;
import org.piotrowski.cardureadoo.domain.model.value.expansion.ExpansionExternalId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardApplicationService implements CardService {
    private final CardRepository cardRepository;

    @Transactional
    @Override
    public Card save(UpsertCardCommand cmd) {

        final var expId = new ExpansionExternalId(cmd.expExternalId());
        final var num = new CardNumber(cmd.cardNumber());
        final var name = new CardName(cmd.cardName());
        final var rarity = new CardRarity(cmd.cardRarity());

        final var card = Card.of(name, rarity, num, expId);
        return cardRepository.save(card);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Card> find(String expExternalId, String cardNumber) {
        return cardRepository.find(new ExpansionExternalId(expExternalId), new CardNumber(cardNumber));
    }

    @Transactional(readOnly = true)
    @Override
    public boolean exists(String expExternalId, String cardNumber) {
        return cardRepository.exists(new ExpansionExternalId(expExternalId), new CardNumber(cardNumber));
    }

//    public record UpsertCardCommand(String expExternalId, String cardNumber, String cardName, String cardRarity) {}
}
