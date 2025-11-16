package org.piotrowski.cardureadoo.infrastructure.persistence.jpa.repositories;

import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.entities.CardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CardJpaRepository extends JpaRepository<CardEntity, Long> {

    Optional<CardEntity> findByExpansionExternalIdAndCardNumber(String externalId, String cardNumber);
    boolean existsByExpansionExternalIdAndCardNumber(String externalId, String cardNumber);
    List<CardEntity> findByExpansionExternalIdOrderByCardNumberAsc(String externalId);
    Page<CardEntity> findByExpansionExternalId(String externalId, Pageable pageable);
    Page<CardEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<CardEntity> findAll(Pageable pageable);

    @Query("select c.id from CardEntity c where c.expansion.externalId = :expExternalId")
    List<Long> findIdsByExpansionExternalId(@Param("expExternalId") String expExternalId);

    @Query("select c.id from CardEntity c where c.expansion.externalId = :expExternalId and c.cardNumber = :cardNumber")
    Optional<Long> findIdByExpansionExternalIdAndCardNumber(@Param("expExternalId") String expExternalId, @Param("cardNumber") String cardNumber);

    @Query("select c.id from CardEntity c where c.expansion.externalId = :expExternalId and c.name = :name")
    List<Long> findIdsByExpansionExternalIdAndName(@Param("expExternalId") String expExternalId, @Param("name") String name);

    @Modifying
    @Query("delete from CardEntity c where c.expansion.externalId = :expExternalId and c.cardNumber = :cardNumber")
    int deleteByExpansionExternalIdAndCardNumber(@Param("expExternalId") String expExternalId, @Param("cardNumber") String cardNumber);

    @Modifying
    @Query("delete from CardEntity c where c.id in :ids")
    int deleteByIds(@Param("ids") List<Long> ids);
}
