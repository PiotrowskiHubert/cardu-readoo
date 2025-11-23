package org.piotrowski.cardureadoo.application.service;

import lombok.RequiredArgsConstructor;
import org.piotrowski.cardureadoo.application.port.in.CardService;
import org.piotrowski.cardureadoo.application.port.out.CardRepository;
import org.piotrowski.cardureadoo.application.port.out.OfferRepository;
import org.piotrowski.cardureadoo.domain.model.Card;
import org.piotrowski.cardureadoo.domain.model.value.card.CardName;
import org.piotrowski.cardureadoo.domain.model.value.card.CardNumber;
import org.piotrowski.cardureadoo.domain.model.value.card.CardRarity;
import org.piotrowski.cardureadoo.domain.model.value.expansion.ExpansionExternalId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardApplicationService implements CardService {

    private final CardRepository cardRepository;
    private final OfferRepository offerRepository;

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
    public List<Card> listByExpansion(String expExternalId, int page, int size) {
        return cardRepository.listByExpansion(new ExpansionExternalId(expExternalId), page, size);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        offerRepository.deleteByCardId(id);
        cardRepository.deleteById(id);
    }

    @Override
    @Transactional
    public int deleteByExpansionAndNumber(String expExternalId, String cardNumber) {
        var idOpt = cardRepository.findIdByExpansionAndNumber(expExternalId, cardNumber);
        if (idOpt.isEmpty()) return 0;
        Long id = idOpt.get();
        offerRepository.deleteByCardId(id);
        cardRepository.deleteById(id);
        return 1;
    }

    @Override
    @Transactional
    public void patch(String expExternalId, String cardNumber, PatchCardCommand cmd) {
        var expId = new ExpansionExternalId(expExternalId);
        var num = new CardNumber(cardNumber);
        var name = (cmd != null && cmd.cardName() != null) ? new CardName(cmd.cardName()) : null;
        var rarity = (cmd != null && cmd.cardRarity() != null) ? new CardRarity(cmd.cardRarity()) : null;

        cardRepository.patch(expId, num, name, rarity);
    }
}

