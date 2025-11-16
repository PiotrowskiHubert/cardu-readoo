package org.piotrowski.cardureadoo.infrastructure.persistence.jpa.repositories;

import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.entities.OfferEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface OfferJpaRepository extends JpaRepository<OfferEntity, Long> {

    List<OfferEntity> findByCardIdAndListedAtBetweenOrderByListedAtAsc(Long cardId, Instant from, Instant to);
    OfferEntity findTopByCardIdOrderByListedAtDesc(Long cardId);
    List<OfferEntity> findByCardIdOrderByListedAtDesc(Long cardId, Pageable pageable);

    @Modifying
    @Query("delete from OfferEntity o where o.id = :id")
    void deleteByIdExplicit(@Param("id") Long id);

    @Modifying
    @Query("delete from OfferEntity o where o.card.id = :cardId")
    void deleteByCardId(@Param("cardId") Long cardId);

    @Modifying
    @Query("delete from OfferEntity o where o.card.id in :cardIds")
    void deleteByCardIds(@Param("cardIds") List<Long> cardIds);

    interface OfferStats {
        BigDecimal getMin();
        BigDecimal getMax();
        BigDecimal getAvg();
        long getCnt();
    }

    @Query("""
            select  min(o.priceAmount) as min, 
                        max(o.priceAmount) as max,
                        avg(o.priceAmount) as avg,
                        count(o) as cnt
            from  OfferEntity  o
            where o.card.id = :cardId and o.listedAt between  :from and :to
                    """)
    OfferStats statsForCardInRange(
            @Param("cardId") Long cardId,
            @Param("from") Instant from,
            @Param("to") Instant to);
}
