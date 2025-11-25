package org.piotrowski.cardureadoo.application.port.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.piotrowski.cardureadoo.domain.model.Expansion;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ExpansionService {

    @Transactional
    Expansion create(CreateExpansionCommand cmd);

    @Transactional(readOnly = true)
    Optional<Expansion> findByName(String expansionName);

    @Transactional(readOnly = true)
    List<Expansion> findAll();

    @Transactional
    void deleteById(Long id);

    @Transactional
    void deleteByName(String name);

    @Transactional
    void patch(String externalId, PatchExpansionCommand cmd);

    record CreateExpansionCommand(
            String externalId,
            String name
    ) {}

    record PatchExpansionCommand(
            String name
    ) {}
}
