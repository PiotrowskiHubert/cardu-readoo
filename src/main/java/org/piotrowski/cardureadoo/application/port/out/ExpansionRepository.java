package org.piotrowski.cardureadoo.application.port.out;

import org.piotrowski.cardureadoo.domain.model.Expansion;
import org.piotrowski.cardureadoo.domain.model.value.expansion.ExpansionExternalId;

import java.util.Optional;

public interface ExpansionRepository {
    Optional<Expansion> findByExternalId(ExpansionExternalId id);
    boolean existsByExternalId(ExpansionExternalId id);
    Expansion save(Expansion expansion);
}
