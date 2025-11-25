package org.piotrowski.cardureadoo.web.dto.expansion;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PatchExpansionRequest(
        @NotBlank String name
) { }
