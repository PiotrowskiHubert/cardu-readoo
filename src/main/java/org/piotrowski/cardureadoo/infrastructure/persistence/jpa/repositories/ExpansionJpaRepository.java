package org.piotrowski.cardureadoo.infrastructure.persistence.jpa.repositories;

import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.entities.ExpansionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExpansionJpaRepository extends JpaRepository<ExpansionEntity, Long> {

    Optional<ExpansionEntity> findByExternalId(String externalId);

    boolean existsByExternalId(String externalId);
}
