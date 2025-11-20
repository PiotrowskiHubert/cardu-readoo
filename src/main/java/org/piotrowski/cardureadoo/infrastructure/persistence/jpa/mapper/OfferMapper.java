package org.piotrowski.cardureadoo.infrastructure.persistence.jpa.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ObjectFactory;
import org.piotrowski.cardureadoo.domain.model.Offer;
import org.piotrowski.cardureadoo.domain.model.value.card.CardNumber;
import org.piotrowski.cardureadoo.domain.model.value.expansion.ExpansionExternalId;
import org.piotrowski.cardureadoo.domain.model.value.offer.Money;
import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.entities.CardEntity;
import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.entities.OfferEntity;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface OfferMapper {

    OfferEntity toEntity(Offer offer, @Context CardEntity card);

    @ObjectFactory
    default OfferEntity newEntity(Offer offer, @Context CardEntity card) {
        Instant listed = offer.getListedAt() != null ? offer.getListedAt() : Instant.now();
        return new OfferEntity(
                card,
                offer.getPrice().amount(),
                offer.getPrice().currency(),
                listed
        );
    }

    default Offer toDomain(OfferEntity e) {
        var expId  = new ExpansionExternalId(e.getCard().getExpansion().getExternalId());
        var number = new CardNumber(e.getCard().getCardNumber());
        var price  = Money.of(e.getPriceAmount(), e.getPriceCurrency());

        return Offer.builder()
                .id(e.getId())
                .expansionId(expId)
                .cardNumber(number)
                .price(price)
                .listedAt(e.getListedAt())
                .build();
    }
}
