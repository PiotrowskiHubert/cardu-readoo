package org.piotrowski.cardureadoo.web.dto.expansion;

import jakarta.validation.constraints.NotBlank;

public record UpsertExpansionRequest(
        @NotBlank String externalId,
        @NotBlank String name
) { }
