package org.piotrowski.cardureadoo.infrastructure.persistence.jpa.adapters;

import lombok.RequiredArgsConstructor;
import org.piotrowski.cardureadoo.application.port.out.OfferRepository;
import org.piotrowski.cardureadoo.domain.model.Offer;
import org.piotrowski.cardureadoo.domain.model.value.card.CardNumber;
import org.piotrowski.cardureadoo.domain.model.value.expansion.ExpansionExternalId;
import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.entities.CardEntity;
import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.mapper.OfferMapper;
import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.repositories.CardJpaRepository;
import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.repositories.OfferJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OfferJpaRepositoryAdapter implements OfferRepository {

    private final OfferJpaRepository offerJpa;
    private final CardJpaRepository cardJpa;
    private final OfferMapper mapper;

    @Override
    @Transactional
    public void save(Offer offer) {
        var card = cardJpa.findByExpansionExternalIdAndCardNumber(
                    offer.getExpansionExternalId().value(),
                        offer.getCardNumber().value())
                .orElseThrow(() -> new IllegalStateException("Card not found for offer"));

        var entity = mapper.toEntity(offer);
        entity.referTo(card);
        offerJpa.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Offer> find(ExpansionExternalId expId, CardNumber number, Instant from, Instant to) {
        var cardId = cardJpa.findByExpansionExternalIdAndCardNumber(expId.value(), number.value())
                .map(CardEntity::getId)
                .orElseThrow(() -> new IllegalStateException("Card not found"));

        return offerJpa
                .findByCardIdAndListedAtBetweenOrderByListedAtAsc(cardId, from, to)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Offer> findLast(ExpansionExternalId expId, CardNumber number) {
        return cardJpa.findByExpansionExternalIdAndCardNumber(expId.value(), number.value())
                .map(CardEntity::getId)
                .map(offerJpa::findTopByCardIdOrderByListedAtDesc)
                .map(mapper::toDomain);
    }
}
