package org.piotrowski.cardureadoo.application.port.in;

import org.piotrowski.cardureadoo.domain.model.Expansion;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ExpansionService {

    @Transactional
    Expansion create(UpsertExpansionCommand cmd);

    @Transactional(readOnly = true)
    Optional<Expansion> findByName(String expansionName);

    @Transactional(readOnly = true)
    List<Expansion> findAll();

    @Transactional
    int deleteById(Long id);

    @Transactional
    boolean deleteByName(String name);

    @Transactional
    void patch(String externalId, PatchExpansionCommand cmd);

    record UpsertExpansionCommand(String externalId, String name) {}
    record PatchExpansionCommand(String name) {}
}
