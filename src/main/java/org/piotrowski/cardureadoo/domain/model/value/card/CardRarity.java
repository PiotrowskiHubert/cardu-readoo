package org.piotrowski.cardureadoo.domain.model.value.card;

public record CardRarity(String value) {
    public static CardRarity of(String raw) {
        return new CardRarity(raw);
    }

    public String normalized() {
        return  value;
    }
}
