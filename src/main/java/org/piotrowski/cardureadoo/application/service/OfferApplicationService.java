package org.piotrowski.cardureadoo.application.service;

import lombok.RequiredArgsConstructor;
import org.piotrowski.cardureadoo.application.exception.web.ResourceNotFoundException;
import org.piotrowski.cardureadoo.application.port.in.OfferService;
import org.piotrowski.cardureadoo.application.port.out.CardRepository;
import org.piotrowski.cardureadoo.application.port.out.ExpansionRepository;
import org.piotrowski.cardureadoo.application.port.out.OfferRepository;
import org.piotrowski.cardureadoo.domain.model.Offer;
import org.piotrowski.cardureadoo.domain.model.value.card.CardNumber;
import org.piotrowski.cardureadoo.domain.model.value.expansion.ExpansionExternalId;
import org.piotrowski.cardureadoo.domain.model.value.offer.Money;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
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
    public long create(CreateOfferCommand cmd) {
        final var expId = new ExpansionExternalId(cmd.expExternalId());
        final var cardNumber = new CardNumber(cmd.cardNumber());
        final var listedAt = (cmd.listedAt() != null) ? cmd.listedAt() : Instant.now();

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

        var persisted = offerRepository.save(Offer.of(expId, cardNumber, price, listedAt));

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

    @Override
    @Transactional
    public void patch(long offerId, PatchOfferCommand cmd) {
        final var currency = (cmd.currency() == null || cmd.currency().isBlank())
                ? "PLN" : cmd.currency().trim().toUpperCase(Locale.ROOT);

        var price = Money.of(cmd.amount(), currency);
        final var listedAt = (cmd.listedAt() != null) ? cmd.listedAt() : Instant.now();

        offerRepository.patch(offerId, price, listedAt);
    }

    @Transactional(readOnly = true)
    @Override
    public List<OfferPointDto> getOffersByCardName(String expExternalId, String cardName, Instant from, Instant to) {
        final var expId = new ExpansionExternalId(expExternalId);
        final var f = from != null ? from : Instant.EPOCH;
        final var t = to != null ? to : Instant.now();

        CardNumber number;
        {
            var allCards = cardRepository.listByExpansion(expId);

            if (allCards.isEmpty()) {
                return Collections.emptyList();
            }

            var optCard = allCards.stream()
                    .filter(c -> c.getNumber() != null && c.getName() != null &&
                            c.getName().value().equals(cardName))
                    .findFirst();

            if (optCard.isEmpty()) {
                return Collections.emptyList();
            }
            number = optCard.get().getNumber();
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

    @Override
    @Transactional
    public void deleteById(Long id) {
        var offerOpt = offerRepository.findById(id);
        if (offerOpt.isEmpty()) {
            throw new ResourceNotFoundException("Offer with id " + id + " not found");
        }

        offerRepository.deleteById(id);
    }
}
