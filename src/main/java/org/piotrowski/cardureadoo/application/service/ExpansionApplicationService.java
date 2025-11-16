package org.piotrowski.cardureadoo.application.service;

import lombok.RequiredArgsConstructor;
import org.piotrowski.cardureadoo.application.port.in.ExpansionService;
import org.piotrowski.cardureadoo.application.port.out.ExpansionRepository;
import org.piotrowski.cardureadoo.domain.model.Expansion;
import org.piotrowski.cardureadoo.domain.model.value.expansion.ExpansionExternalId;
import org.piotrowski.cardureadoo.domain.model.value.expansion.ExpansionName;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExpansionApplicationService implements ExpansionService {

    private final ExpansionRepository expansionRepository;

    @Transactional
    @Override
    public Expansion upsert(UpsertExpansionCommand cmd) {
        final var extId = new ExpansionExternalId(cmd.externalId());
        final var name = new ExpansionName(cmd.name());
        final var exp = new Expansion(extId, name);

        return expansionRepository.save(exp);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Expansion> findByExternalId(String externalId) {
        return expansionRepository.findByExternalId(new ExpansionExternalId(externalId));
    }

    @Transactional(readOnly = true)
    @Override
    public boolean exists(String externalId) {
        return expansionRepository.existsByExternalId(new ExpansionExternalId(externalId));
    }

    public record UpsertExpansionCommand(String externalId, String name) {}
}
