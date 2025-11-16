package org.piotrowski.cardureadoo.application.port.in;

import org.piotrowski.cardureadoo.application.service.OfferApplicationService;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

public interface OfferService {
    @Transactional
    void addOffer(OfferApplicationService.AddOfferCommand cmd);

    @Transactional(readOnly = true)
    List<OfferApplicationService.OfferPointDto> getOffers(String expExternalId, String cardNumber, Instant from, Instant to);

    @Transactional(readOnly = true)
    OfferApplicationService.OfferPointDto getLast(String expExternalId, String cardNumber);

    @Transactional(readOnly = true)
    OfferApplicationService.OfferStatsDto getStats(String expExternalId, String cardNumber, Instant from, Instant to);
}
