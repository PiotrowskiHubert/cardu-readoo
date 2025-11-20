package org.piotrowski.cardureadoo.application.port.in;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface OfferService {

    // C
    @Transactional
    void addOffer(AddOfferCommand cmd);

    // R
    @Transactional(readOnly = true)
    List<OfferPointDto> getOffers(String expExternalId, String cardNumber, Instant from, Instant to);

    @Transactional(readOnly = true)
    List<OfferPointDto> getAll(Instant from, Instant to);

    @Transactional(readOnly = true)
    List<OfferPointDto> getOffersByCardName(String expExternalId, String cardName, Instant from, Instant to);

    @Transactional(readOnly = true)
    OfferPointDto getLast(String expExternalId, String cardNumber);

    @Transactional(readOnly = true)
    OfferStatsDto getStats(String expExternalId, String cardNumber, Instant from, Instant to);

    // D / partial update
    @Transactional
    void deleteById(Long id);

    @Transactional
    void patch(long offerId, PatchOfferCommand cmd);

    record AddOfferCommand(
            String expExternalId,
            String cardNumber,
            String amount,
            String currency,
            Instant listedAt,
//            Long userId
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
