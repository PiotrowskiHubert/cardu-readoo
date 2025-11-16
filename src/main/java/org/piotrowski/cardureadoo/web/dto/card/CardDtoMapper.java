package org.piotrowski.cardureadoo.web.dto.card;

import jakarta.persistence.ManyToOne;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.piotrowski.cardureadoo.domain.model.Card;

@Mapper(componentModel = "spring")
public interface CardDtoMapper {

    @Mapping(target = "expExternalId", source = "expansionId.value")
    @Mapping(target = "cardNumber",    source = "number.value")
    @Mapping(target = "cardName",      source = "name.value")
    @Mapping(target = "cardRarity",    source = "rarityCard.value")
    CardResponse toResponse(Card card);
}
