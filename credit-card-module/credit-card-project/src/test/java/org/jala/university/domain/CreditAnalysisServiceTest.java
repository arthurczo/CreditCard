package org.jala.university.domain;

import org.jala.university.domain.service.CreditAnalysisService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditAnalysisServiceTest {

    @Test
    void analyzeCreditRequest_shouldReject_whenTimeAtJobLessThanMin() {
        CreditAnalysisService service = new CreditAnalysisService();
        BigDecimal monthlyIncome = new BigDecimal("2000");
        int timeAtJobMonths = 5;
        BigDecimal requestedLimit = new BigDecimal("10000");
        BigDecimal currentLimit = BigDecimal.ZERO;

        CreditAnalysisService.CreditAnalysisResult result = service.analyzeCreditRequest(
                monthlyIncome, timeAtJobMonths, requestedLimit, currentLimit);

        assertFalse(result.isApproved());
        assertEquals("Tempo mínimo de emprego não atingido (mínimo 6 meses)", result.getRejectionReason());
    }

    @Test
    void analyzeCreditRequest_shouldReject_whenCreditScoreBelowMin() {
        CreditAnalysisService service = spy(new CreditAnalysisService());
        doReturn(599).when(service).simulateCreditScore();

        BigDecimal monthlyIncome = new BigDecimal("2000");
        int timeAtJobMonths = 6;
        BigDecimal requestedLimit = new BigDecimal("10000");
        BigDecimal currentLimit = BigDecimal.ZERO;

        CreditAnalysisService.CreditAnalysisResult result = service.analyzeCreditRequest(
                monthlyIncome, timeAtJobMonths, requestedLimit, currentLimit);

        assertFalse(result.isApproved());
        assertEquals("Score de crédito insuficiente", result.getRejectionReason());
    }

    @Test
    void analyzeCreditRequest_shouldApproveWithAdjustedLimit_whenRequestedExceedsRecommended() {
        CreditAnalysisService service = spy(new CreditAnalysisService());
        doReturn(700).when(service).simulateCreditScore();

        BigDecimal monthlyIncome = new BigDecimal("2000");
        int timeAtJobMonths = 6;
        BigDecimal requestedLimit = new BigDecimal("15000");
        BigDecimal currentLimit = BigDecimal.ZERO;
        BigDecimal expectedLimit = new BigDecimal("12700.00");

        CreditAnalysisService.CreditAnalysisResult result = service.analyzeCreditRequest(
                monthlyIncome, timeAtJobMonths, requestedLimit, currentLimit);

        assertTrue(result.isApproved());
        assertEquals(expectedLimit, result.getApprovedLimit());
        assertEquals("Solicitação aprovada com limite ajustado", result.getMessage());
    }

    @Test
    void analyzeCreditRequest_shouldApproveWithRequestedLimit_whenRequestedWithinRecommended() {
        CreditAnalysisService service = spy(new CreditAnalysisService());
        doReturn(700).when(service).simulateCreditScore();

        BigDecimal monthlyIncome = new BigDecimal("2000");
        int timeAtJobMonths = 6;
        BigDecimal requestedLimit = new BigDecimal("12000");
        BigDecimal currentLimit = BigDecimal.ZERO;

        CreditAnalysisService.CreditAnalysisResult result = service.analyzeCreditRequest(
                monthlyIncome, timeAtJobMonths, requestedLimit, currentLimit);

        assertTrue(result.isApproved());
        assertEquals(requestedLimit, result.getApprovedLimit());
        assertEquals("Solicitação aprovada com o limite solicitado", result.getMessage());
    }

    @Test
    void analyzeCreditRequest_shouldApproveWithAdjustedLimit_whenLimitIncreaseExceedsMaxFactorAndRecommendedHigherThanCurrent() {
        CreditAnalysisService service = spy(new CreditAnalysisService());
        doReturn(700).when(service).simulateCreditScore();

        BigDecimal monthlyIncome = new BigDecimal("2000");
        int timeAtJobMonths = 6;
        BigDecimal currentLimit = new BigDecimal("10000");
        BigDecimal requestedLimit = new BigDecimal("16000");
        BigDecimal expectedLimit = new BigDecimal("12700.00");

        CreditAnalysisService.CreditAnalysisResult result = service.analyzeCreditRequest(
                monthlyIncome, timeAtJobMonths, requestedLimit, currentLimit);

        assertTrue(result.isApproved());
        assertEquals(expectedLimit, result.getApprovedLimit());
        assertEquals("Aumento aprovado com valor ajustado de acordo com sua capacidade de pagamento", result.getMessage());
    }

    @Test
    void analyzeCreditRequest_shouldReject_whenLimitIncreaseExceedsMaxFactorAndRecommendedLowerThanCurrent() {
        CreditAnalysisService service = spy(new CreditAnalysisService());
        doReturn(600).when(service).simulateCreditScore();

        BigDecimal monthlyIncome = new BigDecimal("1800");
        int timeAtJobMonths = 6;
        BigDecimal currentLimit = new BigDecimal("10000");
        BigDecimal requestedLimit = new BigDecimal("16000");

        CreditAnalysisService.CreditAnalysisResult result = service.analyzeCreditRequest(
                monthlyIncome, timeAtJobMonths, requestedLimit, currentLimit);

        assertFalse(result.isApproved());
        assertEquals("Aumento de limite acima da capacidade de pagamento atual", result.getRejectionReason());
    }
}
