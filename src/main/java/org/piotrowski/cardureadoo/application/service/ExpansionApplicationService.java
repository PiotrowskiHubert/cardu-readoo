package org.piotrowski.cardureadoo.application.service;

import lombok.RequiredArgsConstructor;
import org.piotrowski.cardureadoo.application.exception.expansion.ExpansionAlreadyExistsException;
import org.piotrowski.cardureadoo.application.port.in.ExpansionService;
import org.piotrowski.cardureadoo.application.port.out.CardRepository;
import org.piotrowski.cardureadoo.application.port.out.ExpansionRepository;
import org.piotrowski.cardureadoo.application.port.out.OfferRepository;
import org.piotrowski.cardureadoo.domain.model.Expansion;
import org.piotrowski.cardureadoo.domain.model.value.expansion.ExpansionExternalId;
import org.piotrowski.cardureadoo.domain.model.value.expansion.ExpansionName;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExpansionApplicationService implements ExpansionService {

    private final ExpansionRepository expansionRepository;
    private final CardRepository cardRepository;
    private final OfferRepository offerRepository;

    @Transactional
    @Override
    public Expansion create(UpsertExpansionCommand cmd) {
        final var extId = new ExpansionExternalId(cmd.externalId());
        final var name  = new ExpansionName(cmd.name());
        final var exp   = new Expansion(extId, name);

        try {
            return expansionRepository.save(exp);
        } catch (DataIntegrityViolationException e) {
            throw new ExpansionAlreadyExistsException(
                    "Expansion already exists (name or externalId): " + cmd.name() + " / " + cmd.externalId(),
                    e
            );
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Expansion> findAll() {
        return expansionRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Expansion> findByName(String expansionName) {
        return expansionRepository.findByName(new ExpansionName(expansionName));
    }

    @Override
    @Transactional
    public int deleteById(Long id) {
        var exp = expansionRepository.findById(id).orElse(null);
        if (exp == null) return 0;

        var cardIds = cardRepository.findIdsByExpansion(exp.getId().value());
        if (!cardIds.isEmpty()) {
            offerRepository.deleteByCardIds(cardIds);
            cardRepository.deleteByIds(cardIds);
        }
        return expansionRepository.deleteById(id);
    }

    @Override
    @Transactional
    public boolean deleteByName(String name) {
        var expOpt = expansionRepository.findByName(new ExpansionName(name));
        if (expOpt.isEmpty()) {
            return false;
        }

        var exp = expOpt.get();
        var extId = exp.getId().value();

        var cardIds = cardRepository.findIdsByExpansion(extId);
        if (!cardIds.isEmpty()) {
            offerRepository.deleteByCardIds(cardIds);
            cardRepository.deleteByIds(cardIds);
        }

        int removed = expansionRepository.deleteByExternalId(extId);
        return removed > 0;
    }

    @Override
    @Transactional
    public void patch(String externalId, PatchExpansionCommand cmd) {
        var id = new ExpansionExternalId(externalId);
        var name = cmd != null && cmd.name() != null ? new ExpansionName(cmd.name()) : null;
        expansionRepository.patch(id, name);
    }
}
