package org.piotrowski.cardureadoo.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "CARD",
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_CARD_EXPANSION_CARD_NUMBER", columnNames = {"EXPANSION_ID", "CARD_NUMBER"})
        },
        indexes = {
                @Index(name = "IDX_CARD_EXPANSION_ID", columnList = "EXPANSION_ID"),
                @Index(name = "IDX_CARD_NAME", columnList = "NAME")
        }
)
public class CardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME", nullable = false, length = 200)
    private String name;

    @Column(name = "CARD_NUMBER", nullable = false, length = 32)
    private String cardNumber;

    @Column(name = "CARD_RARITY", nullable = false, length = 32)
    private String cardRarity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "EXPANSION_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_CARD_EXPANSION")
    )
    private ExpansionEntity expansion;

    public CardEntity(String name, String cardNumber, String cardRarity, ExpansionEntity expansion) {
        this.name = name;
        this.cardNumber = cardNumber;
        this.cardRarity = cardRarity;
        this.expansion = expansion;
    }

    public void renameTo(String newName) { this.name = newName; }
    public void changeRarityTo(String newRarity) { this.cardRarity = newRarity; }
    public void attachTo(ExpansionEntity exp) { this.expansion = exp; }
}

