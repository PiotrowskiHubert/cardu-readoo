package org.piotrowski.cardureadoo.domain.model;

import lombok.*;
import org.piotrowski.cardureadoo.domain.model.value.card.CardName;
import org.piotrowski.cardureadoo.domain.model.value.card.CardNumber;
import org.piotrowski.cardureadoo.domain.model.value.card.CardRarity;
import org.piotrowski.cardureadoo.domain.model.value.expansion.ExpansionExternalId;

import java.util.Objects;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Card {
    private CardName name;
    private CardRarity rarityCard;
    private CardNumber number;
    private ExpansionExternalId expansionId;

    public static Card of(
        CardName name,
        CardRarity rarityCard,
        CardNumber number,
        ExpansionExternalId expansionId
    ) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(rarityCard, "rarityCard");
        Objects.requireNonNull(number, "number");
        Objects.requireNonNull(expansionId, "expansionId");

        return Card.builder()
                .name(name)
                .rarityCard(rarityCard)
                .number(number)
                .expansionId(expansionId)
                .build();
    }
}
