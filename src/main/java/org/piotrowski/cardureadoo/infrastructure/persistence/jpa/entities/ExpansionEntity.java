package org.piotrowski.cardureadoo.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "EXPANSION",
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_EXPANSION_EXTERNAL_ID", columnNames = "EXTERNAL_ID"),
                @UniqueConstraint(name = "UK_EXPANSION_NAME", columnNames = "NAME")
        }
)
public class ExpansionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @Column(name = "NAME", nullable = false, length = 200)
    private String name;

    @Column(name = "EXTERNAL_ID", nullable = false, length = 64)
    private String externalId;

    public ExpansionEntity(String name, String externalId) {
        this.name = name;
        this.externalId = externalId;
    }

    public void rename(String newName) { this.name = newName; }
}

