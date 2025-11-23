package org.piotrowski.cardureadoo.application.service;

import lombok.RequiredArgsConstructor;
import org.piotrowski.cardureadoo.application.port.in.OfferService;
import org.piotrowski.cardureadoo.application.port.out.CardRepository;
import org.piotrowski.cardureadoo.application.port.out.ExpansionRepository;
import org.piotrowski.cardureadoo.application.port.out.OfferRepository;
import org.piotrowski.cardureadoo.domain.model.Card;
import org.piotrowski.cardureadoo.domain.model.Offer;
import org.piotrowski.cardureadoo.domain.model.value.card.CardName;
import org.piotrowski.cardureadoo.domain.model.value.card.CardNumber;
import org.piotrowski.cardureadoo.domain.model.value.card.CardRarity;
import org.piotrowski.cardureadoo.domain.model.value.expansion.ExpansionExternalId;
import org.piotrowski.cardureadoo.domain.model.value.offer.Money;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class OfferApplicationService implements OfferService {

    private final OfferRepository offerRepository;
    private final ExpansionRepository expansionRepository;
    private final CardRepository cardRepository;

    @Transactional
    @Override
    public long addOffer(AddOfferCommand cmd) {
        if (cmd.expExternalId() == null || cmd.expExternalId().isBlank()) {
            throw new IllegalArgumentException("expExternalId is required");
        }
        if (cmd.cardNumber() == null || cmd.cardNumber().isBlank()) {
            throw new IllegalArgumentException("cardNumber is required");
        }
        if (cmd.amount() == null) {
            throw new IllegalArgumentException("amount is required");
        }

        final var expId = new ExpansionExternalId(cmd.expExternalId());
        final var cardNumber = new CardNumber(cmd.cardNumber());
        final var when = (cmd.listedAt() != null) ? cmd.listedAt() : Instant.now();

        final var currency = (cmd.currency() == null || cmd.currency().isBlank())
                ? "PLN"
                : cmd.currency().trim().toUpperCase(Locale.ROOT);

        final BigDecimal amount;
        try {
            amount = new BigDecimal(cmd.amount());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("amount must be numeric", e);
        }

        final var price = Money.of(amount, currency);
        final var rarity = new CardRarity(cmd.cardRarity());

        if (!cardRepository.exists(expId, cardNumber)) {
            final var name = (cmd.cardName() != null && !cmd.cardName().isBlank())
                    ? new CardName(cmd.cardName())
                    : new CardName("UNKNOWN");

            cardRepository.save(Card.of(name, rarity, cardNumber, expId));
        }

        var persisted = offerRepository.save(Offer.of(expId, cardNumber, price, when));

        return persisted.getId();
    }

    @Transactional(readOnly = true)
    @Override
    public List<OfferPointDto> getOffers(String expExternalId, String cardNumber, Instant from, Instant to) {
        final var expId = new ExpansionExternalId(expExternalId);
        final var num = new CardNumber(cardNumber);
        final var f = from != null ? from : Instant.EPOCH;
        final var t = to != null ? to : Instant.now();

        return offerRepository.find(expId, num, f, t)
                .stream()
                .map(o -> new OfferPointDto(
                        o.getId(),
                        o.getListedAt(),
                        o.getPrice().amount(),
                        o.getPrice().currency()))
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<OfferPointDto> getAll(Instant from, Instant to) {
        final var f = from != null ? from : Instant.EPOCH;
        final var t = to != null ? to : Instant.now();

        return offerRepository.findAll(f, t).stream()
                .map(o -> new OfferPointDto(
                        o.getId(),
                        o.getListedAt(),
                        o.getPrice().amount(),
                        o.getPrice().currency()
                ))
                .toList();
    }


    @Transactional(readOnly = true)
    @Override
    public OfferPointDto getLast(String expExternalId, String cardNumber) {
        final var expId = new ExpansionExternalId(expExternalId);
        final var num = new CardNumber(cardNumber);

        return offerRepository.findLast(expId, num)
                .map(o -> new OfferPointDto(
                        o.getId(),
                        o.getListedAt(),
                        o.getPrice().amount(),
                        o.getPrice().currency()))
                .orElse(null);
    }

    @Transactional(readOnly = true)
    @Override
    public OfferStatsDto getStats(String expExternalId, String cardNumber, Instant from, Instant to) {
        final var expId = new ExpansionExternalId(expExternalId);
        final var num = new CardNumber(cardNumber);
        final var f = from != null ? from : Instant.EPOCH;
        final var t = to != null ? to : Instant.now();

        final var s = offerRepository.stats(expId, num, f, t);
        return new OfferStatsDto(s.min(), s.max(), s.avg(), s.count());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        offerRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void patch(long offerId, PatchOfferCommand cmd) {
        var money = (cmd != null && (cmd.amount() != null || cmd.currency() != null))
                ? Money.of(cmd.amount() != null ? cmd.amount() : null, cmd.currency()) : null;

        var listedAt = (cmd != null) ? cmd.listedAt() : null;
        offerRepository.patch(offerId, money, listedAt);
    }

    @Transactional(readOnly = true)
    @Override
    public List<OfferPointDto> getOffersByCardName(String expExternalId, String cardName, Instant from, Instant to) {
        final var expId = new ExpansionExternalId(expExternalId);
        final var f = from != null ? from : Instant.EPOCH;
        final var t = to != null ? to : Instant.now();

        var cardIds = cardRepository.findIdsByExpansionAndName(expExternalId, cardName);
        if (cardIds == null || cardIds.isEmpty()) {
            return List.of();
        }

        CardNumber number;
        {
            var allCards = cardRepository.listByExpansion(expId, 0, Integer.MAX_VALUE);
            var maybeCard = allCards.stream()
                    .filter(c -> c.getNumber() != null && c.getName() != null &&
                            c.getName().value().equals(cardName))
                    .findFirst();
            if (maybeCard.isEmpty()) {
                return List.of();
            }
            number = maybeCard.get().getNumber();
        }

        return offerRepository.find(expId, number, f, t)
                .stream()
                .map(o -> new OfferPointDto(
                        o.getId(),
                        o.getListedAt(),
                        o.getPrice().amount(),
                        o.getPrice().currency()))
                .toList();
    }
}
