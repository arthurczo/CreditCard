package org.jala.university.application.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class AdditionalCardRequestDto {
        private UUID mainCardId; // ID do cartão principal ao qual o adicional será vinculado
        // Poderíamos adicionar outros campos aqui se o cartão adicional pudesse ter um titular diferente,
        // mas como a regra é "Mesmo titular do cartão principal", basta o ID do cartão principal.
}

