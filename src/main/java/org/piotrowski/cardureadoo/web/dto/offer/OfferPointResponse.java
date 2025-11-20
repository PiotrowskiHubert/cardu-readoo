package org.piotrowski.cardureadoo.web.dto.offer;

import java.math.BigDecimal;
import java.time.Instant;

public record OfferPointResponse(
        Long id,
        Instant listedAt,
        BigDecimal amount,
        String currency
) { }
