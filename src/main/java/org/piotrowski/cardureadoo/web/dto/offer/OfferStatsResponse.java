package org.piotrowski.cardureadoo.web.dto.offer;

import java.math.BigDecimal;

public record OfferStatsResponse(
        BigDecimal min,
        BigDecimal max,
        BigDecimal avg,
        long count
) { }
