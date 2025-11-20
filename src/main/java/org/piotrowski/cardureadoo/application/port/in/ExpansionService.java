package org.piotrowski.cardureadoo.application.port.in;

import org.piotrowski.cardureadoo.application.service.ExpansionApplicationService;
import org.piotrowski.cardureadoo.domain.model.Expansion;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ExpansionService {

    // C/U
    @Transactional
    Expansion upsert(ExpansionApplicationService.UpsertExpansionCommand cmd);

    // R
    @Transactional(readOnly = true)
    Optional<Expansion> findByExternalId(String externalId);

    @Transactional(readOnly = true)
    Optional<Expansion> findByName(String expansionName);

    @Transactional(readOnly = true)
    boolean exists(String externalId);

    @Transactional(readOnly = true)
    List<Expansion> findAll();

    // D
    @Transactional
    int deleteById(Long id);

    @Transactional
    int deleteByExternalId(String externalId);

    @Transactional
    int deleteByName(String name);

    // partial update
    @Transactional
    void patch(String externalId, PatchExpansionCommand cmd);

    // DTO/commands
    record UpsertExpansionCommand(String externalId, String name) {}
    record PatchExpansionCommand(String name) {}
}
