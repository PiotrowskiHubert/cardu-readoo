package org.piotrowski.cardureadoo.infrastructure.persistence.jpa.mapper;

import org.mapstruct.Mapper;
import org.piotrowski.cardureadoo.domain.model.Expansion;
import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.entities.ExpansionEntity;

@Mapper(componentModel = "spring")
public interface ExpansionMapper {
    ExpansionEntity toEntity(Expansion expansion);
    Expansion toDomain(ExpansionEntity entity);
}
