package org.piotrowski.cardureadoo.infrastructure.persistence.jpa.adapters;

import lombok.RequiredArgsConstructor;
import org.piotrowski.cardureadoo.application.exception.web.ResourceNotFoundException;
import org.piotrowski.cardureadoo.application.port.out.ExpansionRepository;
import org.piotrowski.cardureadoo.domain.model.Expansion;
import org.piotrowski.cardureadoo.domain.model.value.expansion.ExpansionExternalId;
import org.piotrowski.cardureadoo.domain.model.value.expansion.ExpansionName;
import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.mapper.ExpansionMapper;
import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.repositories.ExpansionJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ExpansionJpaRepositoryAdapter implements ExpansionRepository {

    private final ExpansionJpaRepository expansionJpa;
    private final ExpansionMapper mapper;

    @Override
    public Expansion save(Expansion expansion) {
        var entity = mapper.toEntity(expansion);
        var saved = expansionJpa.save(entity);
        expansionJpa.flush();
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Expansion> findAll() {
        return expansionJpa.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Expansion> findById(Long id) {
        return expansionJpa.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Expansion> findByExternalId(ExpansionExternalId id) {
        return expansionJpa.findByExternalId(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<Expansion> findByName(ExpansionName name) {
        return expansionJpa.findByName(name.value()).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void patch(ExpansionExternalId id, ExpansionName name) {
        var e = expansionJpa.findByExternalId(id.value())
                .orElseThrow(() -> new ResourceNotFoundException("Expansion not found: " + id.value()));

        e.rename(name.value());
    }

    @Override
    public int deleteById(Long id) {
        expansionJpa.deleteByIdExplicit(id);
        return 1;
    }

    @Override
    public int deleteByExternalId(String externalId) {
        return expansionJpa.deleteByExternalId(externalId);
    }
}
