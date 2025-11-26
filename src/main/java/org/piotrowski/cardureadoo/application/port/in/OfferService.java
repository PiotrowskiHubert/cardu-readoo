package org.piotrowski.cardureadoo.application.port.in;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface OfferService {

    @Transactional
    long create(CreateOfferCommand cmd);

    @Transactional(readOnly = true)
    List<OfferPointDto> getOffers(String expExternalId, String cardNumber, Instant from, Instant to);

    @Transactional(readOnly = true)
    List<OfferPointDto> getOffersByCardName(String expExternalId, String cardName, Instant from, Instant to);

    @Transactional
    void patch(long offerId, PatchOfferCommand cmd);

    @Transactional
    void deleteById(Long id);

    record CreateOfferCommand(
            String expExternalId,
            String cardNumber,
            String amount,
            String currency,
            Instant listedAt,
            String cardName,
            String cardRarity
    ) {}

    record PatchOfferCommand(
            BigDecimal amount,
            String currency,
            Instant listedAt
    ) {}

    record OfferPointDto(
            Long id,
            Instant listedAt,
            BigDecimal amount,
            String currency
    ) {}

    record OfferStatsDto(
            BigDecimal min,
            BigDecimal  max,
            BigDecimal avg,
            long count
    ) {}
}
