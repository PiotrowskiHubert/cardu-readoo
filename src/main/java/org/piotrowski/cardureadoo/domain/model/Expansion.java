package org.piotrowski.cardureadoo.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.piotrowski.cardureadoo.domain.model.value.expansion.ExpansionExternalId;
import org.piotrowski.cardureadoo.domain.model.value.expansion.ExpansionName;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Expansion {
    private ExpansionExternalId id;
    private ExpansionName name;
}
