package org.piotrowski.cardureadoo.domain.model;

import lombok.*;
import org.piotrowski.cardureadoo.domain.model.value.card.CardNumber;
import org.piotrowski.cardureadoo.domain.model.value.expansion.ExpansionExternalId;
import org.piotrowski.cardureadoo.domain.model.value.offer.Money;

import java.time.Instant;
import java.util.Objects;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access =  AccessLevel.PUBLIC)
public final class Offer {
    private Long id;
    private CardNumber cardNumber;
    private ExpansionExternalId expansionId;
    private Money price;
    private Instant listedAt;

    public static Offer of(
            ExpansionExternalId expansionId,
            CardNumber cardNumber,
            Money price,
            Instant listedAt) {
        var when = (listedAt != null) ? listedAt : Instant.now();
        Objects.requireNonNull(expansionId, "expansionId");
        Objects.requireNonNull(cardNumber, "cardNumber");
        Objects.requireNonNull(price, "price");

        return  Offer.builder()
                .id(null)
                .expansionId(expansionId)
                .cardNumber(cardNumber)
                .price(price)
                .listedAt(when)
                .build();
    }

    public static Offer of(
                            Long id,
                           ExpansionExternalId expId,
                           CardNumber num,
                           Money price,
                           Instant listedAt) {

        return Offer.builder()
                .id(id)
                .expansionId(expId)
                .cardNumber(num)
                .price(price)
                .listedAt(listedAt)
                .build();
    }

}
