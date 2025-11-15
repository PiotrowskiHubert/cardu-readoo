package org.piotrowski.cardureadoo.infrastructure.persistence.jpa.mapper;

import org.mapstruct.Mapper;
import org.piotrowski.cardureadoo.domain.model.Card;
import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.entities.CardEntity;

@Mapper(componentModel = "spring")
public interface CardMapper {
    CardEntity toEntity( Card card);
    Card toDomain(CardEntity entity);
}
