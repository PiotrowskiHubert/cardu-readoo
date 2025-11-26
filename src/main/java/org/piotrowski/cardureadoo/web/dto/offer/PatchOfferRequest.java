package org.piotrowski.cardureadoo.web.dto.offer;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PatchOfferRequest(
        @Pattern(regexp = "^[1-9][0-9]*$", message = "amount must be a positive integer greater than 0")
        @NotBlank BigDecimal amount,
        @Pattern(regexp = "^[A-Z]{3}$", message = "currency must be 3-letter ISO code")
        String currency,
        Instant listedAt
) { }
