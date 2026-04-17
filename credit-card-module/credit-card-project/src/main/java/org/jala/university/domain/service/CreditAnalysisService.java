package org.jala.university.domain.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

@Service
@RequiredArgsConstructor

public class CreditAnalysisService {
    private final Random random = new Random();

    // Constantes para análise de crédito
    private static final BigDecimal MIN_INCOME_MULTIPLIER = new BigDecimal("3");
    private static final BigDecimal MAX_INCOME_MULTIPLIER = new BigDecimal("8");
    private static final int MIN_CREDIT_SCORE = 600;
    private static final int MIN_JOB_STABILITY_MONTHS = 6;
    private static final BigDecimal MAX_LIMIT_INCREASE_FACTOR = new BigDecimal("1.5");

    /**
     * Analisa a solicitação de crédito baseada na renda
     * @param monthlyIncome Renda mensal declarada
     * @param timeAtJobMonths Tempo no emprego atual (em meses)
     * @param requestedLimit Limite de crédito solicitado
     * @param currentLimit Limite atual (0 para novo cartão)
     * @return Resultado da análise de crédito
     */

    public CreditAnalysisResult analyzeCreditRequest(
            BigDecimal monthlyIncome,
            int timeAtJobMonths,
            BigDecimal requestedLimit,
            BigDecimal currentLimit) {

        // Estabilidade no trabalho
        if (timeAtJobMonths < MIN_JOB_STABILITY_MONTHS) {
            return CreditAnalysisResult.rejected(
                    "Tempo mínimo de emprego não atingido (mínimo " + MIN_JOB_STABILITY_MONTHS + " meses)"
            );
        }

        int creditScore = simulateCreditScore();

        // Verificar score mínimo
        if (creditScore < MIN_CREDIT_SCORE) {
            return CreditAnalysisResult.rejected("Score de crédito insuficiente");
        }

        // Calcular limite recomendado
        BigDecimal recommendedLimit = calculateRecommendedLimit(monthlyIncome, creditScore);

        // Para aumento de limite, verificar se é um aumento razoável
        if (currentLimit.compareTo(BigDecimal.ZERO) > 0) {
            // Se o limite solicitado for muito maior que o atual (mais de 50%)
            if (requestedLimit.compareTo(currentLimit.multiply(MAX_LIMIT_INCREASE_FACTOR)) > 0) {
                // Se não puder aprovar o valor solicitado, verifica se pode aprovar o recomendado
                if (recommendedLimit.compareTo(requestedLimit) < 0) {
                    // Se o recomendado for maior que o atual, aprova com o valor recomendado
                    if (recommendedLimit.compareTo(currentLimit) > 0) {
                        return CreditAnalysisResult.approvedWithAdjustedLimit(
                                recommendedLimit,
                                "Aumento aprovado com valor ajustado de acordo com sua capacidade de pagamento"
                        );
                    } else {
                        return CreditAnalysisResult.rejected(
                                "Aumento de limite acima da capacidade de pagamento atual"
                        );
                    }
                }
            }
        }

        // Para novo cartão ou aumento dentro dos limites
        if (requestedLimit.compareTo(recommendedLimit) > 0) {
            return CreditAnalysisResult.approvedWithAdjustedLimit(
                    recommendedLimit,
                    "Solicitação aprovada com limite ajustado"
            );
        }

        // Aprovação total do valor solicitado
        return CreditAnalysisResult.approved(
                requestedLimit,
                "Solicitação aprovada com o limite solicitado"
        );
    }

    /**
     * Cálculo do limite recomendado com base na renda e score de crédito
     */
    public BigDecimal calculateRecommendedLimit(BigDecimal monthlyIncome, int creditScore) {
        BigDecimal normalizedScore = normalizeScore(creditScore);

        // Calcular o multiplicador baseado no score
        BigDecimal multiplier = MIN_INCOME_MULTIPLIER.add(
                normalizedScore.multiply(MAX_INCOME_MULTIPLIER.subtract(MIN_INCOME_MULTIPLIER))
        );

        // Calcular o limite de crédito
        return monthlyIncome.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
    }

    public int simulateCreditScore() {
        return 300 + random.nextInt(600);
    }

    private BigDecimal normalizeScore(int creditScore) {
        // Normalizar o score para um valor entre 0 e 1
        if (creditScore < 300) return BigDecimal.ZERO;
        if (creditScore > 900) return BigDecimal.ONE;

        return new BigDecimal(creditScore - 300)
                .divide(new BigDecimal(600), 2, RoundingMode.HALF_UP);
    }

    /**
     * Classe interna para representar o resultado da análise de crédito
     */
    @Getter
    public static class CreditAnalysisResult {
        private final boolean approved;
        private final BigDecimal approvedLimit;
        private final String message;
        private final String rejectionReason;

        public CreditAnalysisResult(boolean approved, BigDecimal approvedLimit, String message, String rejectionReason) {
            this.approved = approved;
            this.approvedLimit = approvedLimit;
            this.message = message;
            this.rejectionReason = rejectionReason;
        }

        public static CreditAnalysisResult approved(BigDecimal approvedLimit, String message) {
            return new CreditAnalysisResult(true, approvedLimit, message, null);
        }

        public static CreditAnalysisResult approvedWithAdjustedLimit(BigDecimal adjustedLimit, String message) {
            return new CreditAnalysisResult(true, adjustedLimit, message, null);
        }

        public static CreditAnalysisResult rejected(String reason) {
            return new CreditAnalysisResult(false, BigDecimal.ZERO, null, reason);
        }

    }
}