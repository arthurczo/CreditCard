package arthurczo.dev.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditLimitRequestDto {
    private UUID userId;
    private UUID cardId; // Apenas para aumento de limite
    private BigDecimal requestedLimit;
    private BigDecimal monthlyIncome;
    private Integer timeAtCurrentJob; // Em meses
}