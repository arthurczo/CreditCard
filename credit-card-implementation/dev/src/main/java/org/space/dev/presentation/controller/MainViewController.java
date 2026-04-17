package org.jala.university.presentation.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.jala.university.domain.entity.CreditCard;
import org.jala.university.domain.repository.CreditCardRepository;
import org.jala.university.infrastructure.persistance.CreditCardRepositoryMock;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class MainViewController {
    private static MainViewController instance;
    public static MainViewController getInstance() {
        return instance;
    }

    private final CreditCardRepository creditCardRepositoryMock = new CreditCardRepositoryMock();
    private final UUID currentUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    @FXML private BorderPane rootPane;

    @FXML private Button cardRequestButton;
    @FXML private Button invoiceButton;
    @FXML private Button historicButton;
    @FXML private Button increaseLimitButton;
    @FXML private Button notificationsButton;
    @FXML private Button scoreButton;
    @FXML private Button blockButton;
    @FXML private Button otherButton;

    public void setCenterView(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/" + fxmlFileName));
            Node view = loader.load();
            rootPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void handleHomeView() { setCenterView("home-view.fxml");}
    @FXML private void handleCardRequest() { setCenterView("card-request-view.fxml"); }
    @FXML private void handleLimitIncresaseRequest() { setCenterView("limit-increase-request.fxml"); }
    @FXML private void handleInvoiceView() { setCenterView("invoice-view.fxml"); }
    @FXML private void handleAdditionalCardList() { setCenterView("additional-card-list-view.fxml"); }
    @FXML private void handleHistoryView() { setCenterView("history-view.fxml"); }
    @FXML private void handleLimitView() { setCenterView("limit-view.fxml"); }
    @FXML private void handleNotificationsView() { setCenterView("notifications-view.fxml"); }
    @FXML private void handleOtherFeaturesView() { setCenterView("other-features-view.fxml"); }
    @FXML private void handleBlockCardView() { setCenterView("block-view.fxml"); }

    @FXML private void initialize() {
        instance = this;
        loadCreditCardData();
    }

    private void loadCreditCardData() {
        Optional<CreditCard> creditCard = creditCardRepositoryMock.findByUserId(currentUserId);
        if (creditCard.isPresent()) {
            cardRequestButton.setDisable(true);
        } else {
            invoiceButton.setDisable(true);
            historicButton.setDisable(true);
            increaseLimitButton.setDisable(true);
            notificationsButton.setDisable(true);
            scoreButton.setDisable(true);
            blockButton.setDisable(true);
            otherButton.setDisable(true);
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login-view.fxml"));
            Scene loginScene = new Scene(loader.load(), 500, 640);
            loginScene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(loginScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
