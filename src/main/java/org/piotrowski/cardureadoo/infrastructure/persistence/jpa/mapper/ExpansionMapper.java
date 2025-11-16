package org.piotrowski.cardureadoo.infrastructure.persistence.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.piotrowski.cardureadoo.domain.model.Expansion;
import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.entities.ExpansionEntity;

@Mapper(componentModel = "spring")
public interface ExpansionMapper {

    @Mapping(target = "externalId", source = "id.value")
    @Mapping(target = "name",       source = "name.value")
    ExpansionEntity toEntity(Expansion src);

    @Mapping(target = "id",   expression = "java(new ExpansionExternalId(src.getExternalId()))")
    @Mapping(target = "name", expression = "java(new ExpansionName(src.getName()))")
    Expansion toDomain(ExpansionEntity src);
}
