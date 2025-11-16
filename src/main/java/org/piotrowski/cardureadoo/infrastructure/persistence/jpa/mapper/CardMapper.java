package org.piotrowski.cardureadoo.infrastructure.persistence.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.piotrowski.cardureadoo.domain.model.Card;
import org.piotrowski.cardureadoo.domain.model.value.card.CardName;
import org.piotrowski.cardureadoo.domain.model.value.card.CardNumber;
import org.piotrowski.cardureadoo.domain.model.value.card.CardRarity;
import org.piotrowski.cardureadoo.domain.model.value.expansion.ExpansionExternalId;
import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.entities.CardEntity;

@Mapper(
        componentModel = "spring",
        imports = {
                CardName.class,
                CardNumber.class,
                CardRarity.class,
                    ExpansionExternalId.class
        }
)
public interface CardMapper {

    @Mapping(target = "name",       source = "name.value")
    @Mapping(target = "cardNumber", source = "number.value")
    @Mapping(target = "cardRarity", source = "rarityCard.value")
    @Mapping(target = "expansion",  ignore = true)
    CardEntity toEntity(Card src);

    @Mapping(target = "name",        expression = "java(new CardName(src.getName()))")
    @Mapping(target = "number",      expression = "java(new CardNumber(src.getCardNumber()))")
    @Mapping(target = "rarityCard",  expression = "java(new CardRarity(src.getCardRarity()))")
    @Mapping(target = "expansionId", expression = "java(new ExpansionExternalId(src.getExpansion().getExternalId()))")
    Card toDomain(CardEntity src);
}
