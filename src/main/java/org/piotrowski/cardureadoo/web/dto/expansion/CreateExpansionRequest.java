package org.piotrowski.cardureadoo.web.dto.expansion;

import jakarta.validation.constraints.NotBlank;

public record CreateExpansionRequest(
        @NotBlank String externalId,
        @NotBlank String name
) { }
