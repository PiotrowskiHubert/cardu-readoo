package org.piotrowski.cardureadoo.infrastructure.persistence.jpa.repositories;

import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.entities.ExpansionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExpansionJpaRepository extends JpaRepository<ExpansionEntity, Long> {

    Optional<ExpansionEntity> findByExternalId(String externalId);

    boolean existsByExternalId(String externalId);

    @Query("select e.id from ExpansionEntity e where e.externalId = :externalId")
    Optional<Long> findIdByExternalId(@Param("externalId") String externalId);

    @Query("select e.id from ExpansionEntity e where e.name = :name")
    List<Long> findIdsByName(@Param("name") String name);

    @Modifying
    @Query("delete from ExpansionEntity e where e.id = :id")
    void deleteByIdExplicit(@Param("id") Long id);

    @Modifying
    @Query("delete from ExpansionEntity e where e.externalId = :externalId")
    int deleteByExternalId(@Param("externalId") String externalId);

    @Modifying
    @Query("delete from ExpansionEntity e where e.name = :name")
    int deleteByName(@Param("name") String name);
}
