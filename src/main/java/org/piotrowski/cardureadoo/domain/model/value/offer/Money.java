package org.piotrowski.cardureadoo.domain.model.value.offer;

import java.math.BigDecimal;

public record Money(BigDecimal amount, String currency) {

    public static final String DEFAULT_CURRENCY = "PLN";

    public static Money of(BigDecimal amount) {
        return new Money(amount, DEFAULT_CURRENCY);
    }

    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }

    public static Money of(double amount) {
        return new Money(BigDecimal.valueOf(amount), DEFAULT_CURRENCY);
    }
}
