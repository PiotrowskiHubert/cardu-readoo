package org.piotrowski.cardureadoo.domain.model.value.card;

public record CardNumber(String value) {

    public static CardNumber of (String raw) {
        return new CardNumber(raw);
    }

    public String normalized() {
        return value;
    }
}
