package org.piotrowski.cardureadoo.web.dto.card;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PatchCardRequest(
        @NotBlank String name,
        @NotBlank String rarity
) { }
