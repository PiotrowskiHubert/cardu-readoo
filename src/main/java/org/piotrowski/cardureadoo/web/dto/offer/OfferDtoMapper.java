package org.piotrowski.cardureadoo.web.dto.offer;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.piotrowski.cardureadoo.application.port.in.OfferService.AddOfferCommand;
import org.piotrowski.cardureadoo.application.port.in.OfferService.OfferPointDto;
import org.piotrowski.cardureadoo.application.port.in.OfferService.OfferStatsDto;

@Mapper(componentModel = "spring")
public interface OfferDtoMapper {

    AddOfferCommand toCommand(AddOfferRequest req);

    @Mapping(target = "id", source = "id")
    OfferPointResponse toResponse(OfferPointDto dto);

    OfferStatsResponse toResponse(OfferStatsDto dto);
}
