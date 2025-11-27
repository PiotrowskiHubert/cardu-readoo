package org.piotrowski.cardureadoo.web.dto.offer;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PatchOfferRequest(
        @Positive(message = "amount must be a positive number greater than 0")
        BigDecimal amount,
        @Pattern(regexp = "^[A-Z]{3}$", message = "currency must be 3-letter ISO code")
        String currency,
        Instant listedAt
) { }
