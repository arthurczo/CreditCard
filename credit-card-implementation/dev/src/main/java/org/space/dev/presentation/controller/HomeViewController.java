package arthurczo.dev.presentation.controller;

import com.sun.tools.javac.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import arthurczo.dev.domain.entity.CreditCard;
import arthurczo.dev.domain.repository.CreditCardRepository;
import arthurczo.dev.infrastructure.persistance.CreditCardGenerator;
import arthurczo.dev.infrastructure.persistance.CreditCardRepositoryMock;
import org.kordamp.ikonli.javafx.FontIcon;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public class HomeViewController {
    private final CreditCardRepository creditCardRepositoryMock = new CreditCardRepositoryMock();
    private final UUID currentUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    @FXML private StackPane cardContainer;
    @FXML private GridPane cardDetails;

    @FXML private Label messageLabel;

    @FXML private Label cardNumberLabel;
    @FXML private Label cardHolderLabel;
    @FXML private Label cardExpiryLabel;
    @FXML private Label cardCvvLabel;

    @FXML private Label limitTotalLabel;
    @FXML private Label limitAvailableLabel;
    @FXML private Label currentInvoiceLabel;
    @FXML private Label dueDateLabel;

    @FXML private Label statusLabel;
    @FXML private FontIcon statusIcon;

    @FXML
    public void initialize() {
        loadCreditCardData();
    }

    private void loadCreditCardData() {
        Optional<CreditCard> creditCard = creditCardRepositoryMock.findByUserId(currentUserId);

        if (creditCard.isPresent()) {
            displayCreditCardInfo(creditCard.get());

            cardContainer.setVisible(true);
            cardDetails.setVisible(true);
            messageLabel.setVisible(false);
        } else {
            cardContainer.setVisible(false);
            cardDetails.setVisible(false);
            messageLabel.setVisible(true);
        }
    }

    private void displayCreditCardInfo(CreditCard card) {
        // Formatting currency values
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        String creditLimit = currencyFormatter.format(card.getCreditLimit());
        String availableLimit = currencyFormatter.format(card.getAvailableLimit());

        cardHolderLabel.setText(card.getName());
        cardNumberLabel.setText(card.getNumber());
        cardCvvLabel.setText(String.valueOf(card.getCvv()));
        statusLabel.setText(formatCardStatus(card.getStatus()));

        if (card.getStatus() == CreditCard.Status.ACTIVE) {
            statusLabel.getStyleClass().add("status-active-label");
            statusIcon.setIconLiteral("fas-check-circle");
            statusIcon.getStyleClass().add("status-active-icon");
        } else {
            statusLabel.getStyleClass().add("status-blocked-label");
            statusIcon.setIconLiteral("fas-lock");
            statusIcon.getStyleClass().add("status-blocked-icon");
        }

        limitTotalLabel.setText(String.valueOf(card.getCreditLimit()));
        limitAvailableLabel.setText(String.valueOf(card.getAvailableLimit()));
    }

    private String formatCardStatus(CreditCard.Status status) {
        return switch (status) {
            case ACTIVE -> "Ativo";
            case BLOCKED -> "Bloqueado";
            case PENDING -> "Pendente";
            default -> status.toString();
        };
    }

    @FXML
    private void handleRequestCardButton() {
        CreditCard randomCard = CreditCardGenerator.buildRandomCreditCard();
        randomCard.setUserId(currentUserId);
        randomCard.setStatus(CreditCard.Status.PENDING);

        CreditCard savedCard = creditCardRepositoryMock.save(
                CreditCard.builder()
                        .id(randomCard.getId())
                        .userId(randomCard.getUserId())
                        .number(randomCard.getNumber())
                        .cvv(randomCard.getCvv())
                        .creditLimit(randomCard.getCreditLimit())
                        .availableLimit(randomCard.getAvailableLimit())
                        .status(randomCard.getStatus())
                        .build()
        );

        loadCreditCardData();
    }

}