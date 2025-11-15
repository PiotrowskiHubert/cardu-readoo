package org.piotrowski.cardureadoo.infrastructure.persistence.jpa.mapper;

import org.mapstruct.Mapper;
import org.piotrowski.cardureadoo.domain.model.Offer;
import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.entities.OfferEntity;

@Mapper(componentModel = "spring")
public interface OfferMapper {
    OfferEntity toEntity(Offer offer);
    Offer toDomain(OfferEntity entity);
}
