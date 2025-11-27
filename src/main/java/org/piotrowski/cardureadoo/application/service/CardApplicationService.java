package org.piotrowski.cardureadoo.application.service;

import lombok.RequiredArgsConstructor;
import org.piotrowski.cardureadoo.application.exception.web.ResourceNotFoundException;
import org.piotrowski.cardureadoo.application.port.in.CardService;
import org.piotrowski.cardureadoo.application.port.in.ExpansionService;
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

@Service
@RequiredArgsConstructor
public class CardApplicationService implements CardService {

    private final CardRepository cardRepository;
    private final OfferRepository offerRepository;
    private final ExpansionService expansionService;

    @Transactional
    @Override
    public Card create(CreateCardCommand cmd) {
        final var expId = new ExpansionExternalId(cmd.expExternalId());
        final var num = new CardNumber(cmd.cardNumber());
        final var name = new CardName(cmd.cardName());
        final var rarity = new CardRarity(cmd.cardRarity());

        final var card = Card.of(name, rarity, num, expId);
        return cardRepository.save(card);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Card> getByExpansionName(String expansionName) {
        var expExternalIdOpt = expansionService.findByName(expansionName);

        if (expExternalIdOpt.isEmpty()) {
            throw new ResourceNotFoundException("Expansion not found: " + expansionName);
        }
        var expExternalId = expExternalIdOpt.get().getId().value();

        return cardRepository.listByExpansion(new ExpansionExternalId(expExternalId));
    }

    @Override
    @Transactional
    public void patch(String expExternalId, String cardNumber, PatchCardCommand cmd) {
        if (cardNumber.isBlank()) {
            throw new IllegalArgumentException("cardNumber cannot be empty or blank");
        }

        var expId = new ExpansionExternalId(expExternalId);
        var num = new CardNumber(cardNumber);
        var name = new CardName(cmd.cardName());
        var rarity = new CardRarity(cmd.cardRarity());

        cardRepository.patch(expId, num, name, rarity);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        var cardOpt = cardRepository.findById(id);
        if (cardOpt.isEmpty()) {
            throw new ResourceNotFoundException("Card not found with id: " + id);
        }

        offerRepository.deleteByCardId(id);
        cardRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByExpansionAndNumber(String expExternalId, String cardNumber) {
        if (expExternalId.isBlank() || cardNumber.isBlank()) {
            throw new IllegalArgumentException("expansionExternalId and cardNumber cannot be empty or blank");
        }

        var cardIdOpt = cardRepository.findIdByExpansionAndNumber(expExternalId, cardNumber);
        if (cardIdOpt.isEmpty()) {
            throw new ResourceNotFoundException("Card not found with expansionExternalId: " + expExternalId + " and cardNumber: " + cardNumber);
        }

        var id = cardIdOpt.get();

        offerRepository.deleteByCardId(id);
        cardRepository.deleteById(id);
    }
}
