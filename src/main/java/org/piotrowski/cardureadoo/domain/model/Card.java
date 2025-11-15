package org.piotrowski.cardureadoo.domain.model;

import lombok.*;
import org.piotrowski.cardureadoo.domain.model.value.card.CardName;
import org.piotrowski.cardureadoo.domain.model.value.card.CardNumber;
import org.piotrowski.cardureadoo.domain.model.value.card.CardRarity;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Card {
    private CardName name;
    private CardRarity rarityCard;
    private CardNumber number;
    private Expansion expansion;
}
