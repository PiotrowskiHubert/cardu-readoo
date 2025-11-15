package org.piotrowski.cardureadoo.infrastructure.persistence.jpa.repositories;

import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.entities.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardJpaRepository extends JpaRepository<CardEntity, Long> {

    Optional<CardEntity> findByExpansionExternalIdAndCardNumber(String externalId, String cardNumber);
    boolean existsByExpansionExternalIdAndCardNumber(String externalId, String cardNumber);
    List<CardEntity> findByExpansionExternalIdOrderByCardNumberAsc(String externalId);
}
