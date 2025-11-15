package org.piotrowski.cardureadoo.domain.model.value.expansion;

public record ExpansionExternalId(String value) {

    public static ExpansionExternalId of (String raw) {
        return  new ExpansionExternalId(raw);
    }

    public String normalized() {
        return value;
    }
}
