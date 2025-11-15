package org.piotrowski.cardureadoo.domain.model.value.expansion;

public record ExpansionName(String value) {

    public static ExpansionName of (String raw) {
        return  new ExpansionName(raw);
    }

    public String normalized() {
        return value;
    }
}
