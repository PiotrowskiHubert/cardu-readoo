package org.piotrowski.cardureadoo.domain.model.value.card;

public record CardName(String value) {

    public  static  CardName of (String raw) {
        return new CardName(raw);
    }

    public String normalized() {
        return value;
    }

}
