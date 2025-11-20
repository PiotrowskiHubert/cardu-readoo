package org.piotrowski.cardureadoo.application.port.out;

import org.piotrowski.cardureadoo.domain.model.Expansion;
import org.piotrowski.cardureadoo.domain.model.value.expansion.ExpansionExternalId;
import org.piotrowski.cardureadoo.domain.model.value.expansion.ExpansionName;

import java.util.List;
import java.util.Optional;

public interface ExpansionRepository {

    // C/U
    Expansion save(Expansion expansion);

    // R
    Optional<Expansion> findById(Long id);

    Optional<Expansion> findByExternalId(ExpansionExternalId id);

    Optional<Expansion> findByName(ExpansionName name);

    boolean existsByExternalId(ExpansionExternalId id);

    List<Expansion> findAll();

    // D
    int deleteById(Long id);

    int deleteByExternalId(String externalId);

    // partial update
    void patch(ExpansionExternalId id, ExpansionName name);
}
