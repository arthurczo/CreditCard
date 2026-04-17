package org.jala.university.presentation.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.EqualsAndHashCode;
import org.jala.university.application.service.CreditCardService;
import org.jala.university.commons.presentation.BaseController;
import org.jala.university.commons.presentation.ViewSwitcher;
import org.jala.university.domain.entity.CreditCard;
import org.jala.university.domain.repository.CreditCardRepository;
import org.jala.university.infrastructure.persistance.CreditCardGenerator;
import org.jala.university.infrastructure.persistance.CreditCardRepositoryMock;
import org.jala.university.presentation.CreditCardView;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;

import javafx.fxml.Initializable;
import org.springframework.stereotype.Component;

@EqualsAndHashCode(callSuper = true)
@Component
public class CreditCardViewController extends BaseController implements Initializable {
    private CreditCardService creditCardService;
    private final CreditCardRepository creditCardRepositoryMock = new CreditCardRepositoryMock();
    private final UUID currentUserId = UUID.fromString("23e4567-e89b-12d3-a456-426614174000");

//    public CreditCardViewController(CreditCardService creditCardService) {
//        this.creditCardService = creditCardService;
//    }

    public CreditCardViewController() {}

    @FXML private HBox cardInfoContainer;
    @FXML private VBox noCardContainer;
    @FXML private Button requestCardButton;

    @FXML private Label cardHolderNameLabel;
    @FXML private Label cardNumberLabel;
    @FXML private Label cardCvvLabel;
    @FXML private Label cardStatusLabel;
    @FXML private Label invoiceMonthLabel;

    @FXML private Label invoiceAmountLabel;
    @FXML private Button payButton;
    @FXML private Button detailsButton;

    @FXML private Button transactionsButton;
    @FXML private Button invoicesButton;
    @FXML private Button blockButton;
    @FXML private Button increaseLimitButton;
    @FXML private Button exitButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupIcons();
        loadCreditCardData();
    }

    private void setupIcons() {
        FontIcon chartIcon = new FontIcon(FontAwesomeSolid.CHART_BAR);
        chartIcon.setIconSize(18);
        transactionsButton.setGraphic(chartIcon);

        FontIcon fileIcon = new FontIcon(FontAwesomeSolid.FILE_INVOICE);
        fileIcon.setIconSize(18);
        invoicesButton.setGraphic(fileIcon);

        FontIcon banIcon = new FontIcon(FontAwesomeSolid.BAN);
        banIcon.setIconSize(18);
        blockButton.setGraphic(banIcon);

        FontIcon arrowUpIcon = new FontIcon(FontAwesomeSolid.ARROW_UP);
        arrowUpIcon.setIconSize(18);
        increaseLimitButton.setGraphic(arrowUpIcon);

        FontIcon exitIcon = new FontIcon(FontAwesomeSolid.ARROW_LEFT);
        exitIcon.setIconSize(18);
        exitButton.setGraphic(exitIcon);
    }

    private void loadCreditCardData() {
//        Optional<CreditCardEntityDto> creditCard = creditCardService.getCreditCardByUserId(currentUserId);
        Optional<CreditCard> creditCard = creditCardRepositoryMock.findByUserId(currentUserId);

        if (creditCard.isPresent()) {
            displayCreditCardInfo(creditCard.get());
            cardInfoContainer.setVisible(true);
            cardInfoContainer.setManaged(true);

            transactionsButton.setDisable(false);
            invoicesButton.setDisable(false);
            blockButton.setDisable(false);
            increaseLimitButton.setDisable(false);

            noCardContainer.setVisible(false);
            noCardContainer.setManaged(false);
        } else {
            cardInfoContainer.setVisible(false);
            cardInfoContainer.setManaged(false);

            transactionsButton.setDisable(true);
            invoicesButton.setDisable(true);
            blockButton.setDisable(true);
            increaseLimitButton.setDisable(true);

            noCardContainer.setVisible(true);
            noCardContainer.setManaged(true);
        }
    }

    private void displayCreditCardInfo(CreditCard card) {
        // Formatting currency values
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        String creditLimit = currencyFormatter.format(card.getCreditLimit());
        String availableLimit = currencyFormatter.format(card.getAvailableLimit());

        cardHolderNameLabel.setText("Nome do titular: " + card.getName());
        cardNumberLabel.setText("Número: " + card.getNumber());
        cardCvvLabel.setText("CVV: " + card.getCvv());
        cardStatusLabel.setText("Status: " + formatCardStatus(card.getStatus()));

        // creditLimitLabel.setText("Limite: " + creditLimit);
        // availableLimitLabel.setText("Disponível: " + availableLimit);

        invoiceMonthLabel.setText("Abril");
        invoiceAmountLabel.setText("R$ 1.589,90");
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

    @FXML
    private void handleInvoicesButton() {
        ViewSwitcher.switchTo(CreditCardView.INVOICE.getView());
    }

    @FXML
    private void handleTransactionsButton() {
        ViewSwitcher.switchTo(CreditCardView.TRANSACTIONS.getView());
    }

    @FXML
    private void handleBlockButton() {
        ViewSwitcher.switchTo(CreditCardView.BLOCK.getView());
    }

    @FXML
    private void handleIncreaseLimitButton() {
        ViewSwitcher.switchTo(CreditCardView.INCREASE_LIMIT.getView());
    }

}
