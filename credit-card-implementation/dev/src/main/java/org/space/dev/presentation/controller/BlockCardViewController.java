package arthurczo.dev.presentation.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import arthurczo.dev.domain.entity.CreditCard;
import arthurczo.dev.domain.entity.CreditCard.Status;
import arthurczo.dev.domain.repository.CreditCardRepository;
import arthurczo.dev.infrastructure.persistance.CreditCardRepositoryMock;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Optional;
import java.util.UUID;

public class BlockCardViewController {
    private final CreditCardRepository creditCardRepositoryMock = new CreditCardRepositoryMock();
    private final UUID currentUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    Optional<CreditCard> creditCard = Optional.empty();

    @FXML private Label statusLabel;
    @FXML private FontIcon statusIcon;

    @FXML private Button blockButton;
    @FXML private Button unblockButton;
    @FXML private Button simulateButton;

    @FXML
    public void initialize() {
        creditCard = creditCardRepositoryMock.findByUserId(currentUserId);
        unblockButton.setDisable(true);
        updateStatusLabel();
    }

    @FXML
    public void onBlockCard() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Bloquear Cartão");
        dialog.setHeaderText("Informe o motivo do bloqueio:");
        dialog.setContentText("Motivo:");

        dialog.showAndWait().ifPresent(motivo -> {
            creditCardRepositoryMock.updateStatus(creditCard.get().getId(), Status.BLOCKED);
            updateCreditCardFromRepository();

            blockButton.setDisable(true);
            unblockButton.setDisable(false);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Cartão Bloqueado");
            alert.setHeaderText(null);
            alert.setContentText("Cartão bloqueado com sucesso.\nMotivo: " + motivo);
            alert.showAndWait();
        });
    }

    @FXML
    public void onUnblockCard() {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Tem certeza que deseja desbloquear?");

        confirmationAlert.setResultConverter(buttonType -> {
            if (buttonType != ButtonType.OK) {
                return null;
            }

            return buttonType;
        });

        confirmationAlert.showAndWait().ifPresent(motivo -> {
            creditCardRepositoryMock.updateStatus(creditCard.get().getId(), Status.ACTIVE);
            updateCreditCardFromRepository();

            unblockButton.setDisable(true);
            blockButton.setDisable(false);

            Alert feedbackAlert = new Alert(Alert.AlertType.INFORMATION);
            feedbackAlert.setTitle("Cartão Desbloqueado");
            feedbackAlert.setHeaderText(null);
            feedbackAlert.setContentText("Cartão desbloqueado com sucesso.");
            feedbackAlert.showAndWait();
        });
    }

    @FXML
    public void onSimulateTransaction() {
        updateCreditCardFromRepository();
        if (creditCard.get().getStatus().equals(Status.ACTIVE)) {
            new Alert(Alert.AlertType.INFORMATION, "Transação realizada com sucesso!").showAndWait();
        } else {
            new Alert(Alert.AlertType.ERROR, "Cartão bloqueado. Transação negada.").showAndWait();
        }
    }

    private void updateStatusLabel() {
        statusLabel.setText("Status: " + creditCard.get().getStatus());

        if (creditCard.get().getStatus() == CreditCard.Status.ACTIVE) {
            statusLabel.getStyleClass().add("status-active-label");
            statusLabel.getStyleClass().remove("status-blocked-label");

            statusIcon.getStyleClass().add("status-active-icon");
            statusIcon.getStyleClass().remove("status-blocked-icon");

            statusIcon.setIconLiteral("fas-check-circle");
        } else {
            statusLabel.getStyleClass().add("status-blocked-label");
            statusLabel.getStyleClass().remove("status-active-label");

            statusIcon.getStyleClass().add("status-blocked-icon");
            statusIcon.getStyleClass().remove("status-active-icon");

            statusIcon.setIconLiteral("fas-lock");
        }
    }

    private void updateCreditCardFromRepository() {
        creditCard = creditCardRepositoryMock.findByUserId(currentUserId);
        updateStatusLabel();
    }
}
