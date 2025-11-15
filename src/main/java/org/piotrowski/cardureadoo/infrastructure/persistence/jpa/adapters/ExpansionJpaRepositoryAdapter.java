package org.piotrowski.cardureadoo.infrastructure.persistence.jpa.adapters;

import lombok.RequiredArgsConstructor;
import org.piotrowski.cardureadoo.application.port.out.ExpansionRepository;
import org.piotrowski.cardureadoo.domain.model.Expansion;
import org.piotrowski.cardureadoo.domain.model.value.expansion.ExpansionExternalId;
import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.mapper.ExpansionMapper;
import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.repositories.ExpansionJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ExpansionJpaRepositoryAdapter implements ExpansionRepository {

    private final ExpansionJpaRepository expansionJpa;
    private final ExpansionMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<Expansion> findByExternalId(ExpansionExternalId id) {
        return expansionJpa.findByExternalId(id.value()).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByExternalId(ExpansionExternalId id) {
        return expansionJpa.existsByExternalId(id.value());
    }

    @Override
    public Expansion save(Expansion expansion) {
        var entity = mapper.toEntity(expansion);
        return mapper.toDomain(expansionJpa.save(entity));
    }
}
