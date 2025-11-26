package org.piotrowski.cardureadoo.application.service;

import lombok.RequiredArgsConstructor;
import org.piotrowski.cardureadoo.application.exception.expansion.ExpansionAlreadyExistsException;
import org.piotrowski.cardureadoo.application.exception.web.ResourceNotFoundException;
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
    public Expansion create(CreateExpansionCommand cmd) {
        final var extId = new ExpansionExternalId(cmd.externalId());
        final var name  = new ExpansionName(cmd.name());
        final var exp   = new Expansion(extId, name);
        return expansionRepository.save(exp);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Expansion> findAll() {
        var expansions = expansionRepository.findAll();
        return expansions;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Expansion> findByName(String expansionName) {
        var expOpt = expansionRepository.findByName(new ExpansionName(expansionName));

        if (expOpt.isEmpty()) {
            throw  new ResourceNotFoundException("Expansion not found: " + expansionName);
        }

        return expOpt;
    }

    @Override
    @Transactional
    public void patch(String externalId, PatchExpansionCommand cmd) {
        var id = new ExpansionExternalId(externalId);
        var expName = new ExpansionName(cmd.name());
        expansionRepository.patch(id, expName);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        var expOpt = expansionRepository.findById(id);
        if (expOpt.isEmpty()) {
            throw  new ResourceNotFoundException("Expansion with id " + id + " not found");
        }

        var exp = expOpt.get();
        var extId = exp.getId().value();

        var cardIds = cardRepository.findIdsByExpansion(extId);

        if (cardIds.isEmpty()) {
            throw   new ResourceNotFoundException("Card with id " + id + " not found");
        }

        offerRepository.deleteByCardIds(cardIds);
        cardRepository.deleteByIds(cardIds);
        expansionRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByName(String name) {
        var expOpt = expansionRepository.findByName(new ExpansionName(name));
        if (expOpt.isEmpty()) {
            throw new ResourceNotFoundException("Expansion not found: " + name);
        }

        var exp = expOpt.get();
        var extId = exp.getId().value();

        var cardIds = cardRepository.findIdsByExpansion(extId);
        if (!cardIds.isEmpty()) {
//            throw new ResourceNotFoundException("Card not found: " + cardIds);
            offerRepository.deleteByCardIds(cardIds);
            cardRepository.deleteByIds(cardIds);
        }


        expansionRepository.deleteByExternalId(extId);
    }
}
