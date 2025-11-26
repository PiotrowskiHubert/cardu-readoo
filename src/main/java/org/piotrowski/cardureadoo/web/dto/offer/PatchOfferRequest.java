package org.piotrowski.cardureadoo.web.dto.offer;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PatchOfferRequest(
        @NotBlank BigDecimal amount,
        String currency,
        Instant listedAt
) { }
