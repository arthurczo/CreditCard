package org.jala.university.presentation.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.jala.university.application.dto.CreditLimitRequestDto;
import org.jala.university.domain.entity.CreditCard;
import org.jala.university.infrastructure.persistance.CreditCardRepositoryMock;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
public class LimitIncreaseViewController {
//    private CreditCardService creditCardService;
    private CreditCardRepositoryMock creditCardRepository = new CreditCardRepositoryMock();
    private Optional<CreditCard> creditCard;

    private final UUID currentUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    @FXML Label currentLimitLabel;

    @FXML TextField requestedLimitField;
    @FXML TextField monthlyIncomeField;
    @FXML TextField timeAtCurrentJobField;

    @FXML
    public void initialize() {
//        creditCard = creditCardService.getCreditCardByUserId(currentUserId);
        creditCard = creditCardRepository.findByUserId(currentUserId);
        currentLimitLabel.setText("Limite atual: " + creditCard.get().getAvailableLimit());
    }

    @FXML
    private void onRequestLimitIncrease() {
        CreditLimitRequestDto requestDto = CreditLimitRequestDto.builder()
                .userId(currentUserId)
                .cardId(creditCard.get().getId())
                .requestedLimit(new BigDecimal(requestedLimitField.getText()))
                .monthlyIncome(new BigDecimal(monthlyIncomeField.getText()))
                .timeAtCurrentJob(Integer.parseInt(timeAtCurrentJobField.getText()))
                .build();

//        CreditRequestResultDto result = creditCardService.requestCreditIncrease(requestDto);
//
//        if (result.isApproved()) {
//            new Alert(Alert.AlertType.INFORMATION, "Novo limite de crédito aprovado: " + result.getApprovedLimit());
//        } else {
//            new Alert(Alert.AlertType.ERROR, "Aumento de limite recusado. Motivo: " + result.getRejectionReason());
//        }
    }
}
