package org.piotrowski.cardureadoo.infrastructure.persistence.jpa.adapters;

import lombok.RequiredArgsConstructor;
import org.piotrowski.cardureadoo.application.port.out.CardRepository;
import org.piotrowski.cardureadoo.domain.model.Card;
import org.piotrowski.cardureadoo.domain.model.value.card.CardNumber;
import org.piotrowski.cardureadoo.domain.model.value.expansion.ExpansionExternalId;
import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.mapper.CardMapper;
import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.repositories.CardJpaRepository;
import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.repositories.ExpansionJpaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CardJpaRepositoryAdapter implements CardRepository {

    private final CardJpaRepository cardJpa;
    private final ExpansionJpaRepository expansionJpa;
    private final CardMapper mapper;


    @Override
    @Transactional(readOnly = true)
    public Optional<Card> find(ExpansionExternalId expId, CardNumber number) {
        return cardJpa
                .findByExpansionExternalIdAndCardNumber(expId.value(), number.value())
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean exists(ExpansionExternalId expId, CardNumber number) {
        return cardJpa.existsByExpansionExternalIdAndCardNumber(expId.value(), number.value());
    }

    @Override
    @Transactional
    public Card save(Card card) {
        var exp = expansionJpa.findByExternalId(card.getExpansion().getId().value())
                .orElseThrow(() -> new IllegalStateException("Expansion not found: " + card.getExpansion().getId().value()));

        var existing = cardJpa.findByExpansionExternalIdAndCardNumber(card.getExpansion().getId().value(), card.getNumber().value());

        if (existing.isPresent()) {
            var e = existing.get();
            e.renameTo(card.getName().value());
            e.changeRarityTo(card.getRarityCard().value());
            return mapper.toDomain(cardJpa.save(e));
        }

        try {
            var entity  = mapper.toEntity(card);
            entity.attachTo(exp);
            return mapper.toDomain(cardJpa.save(entity));
        } catch (DataIntegrityViolationException dup) {
            return cardJpa.findByExpansionExternalIdAndCardNumber(
                            card.getExpansion().getId().value(), card.getNumber().value())
                    .map(mapper::toDomain)
                    .orElseThrow(() -> dup);
        }
    }
}
