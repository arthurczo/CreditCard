package arthurczo.dev.presentation.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class AdditionalCardListController {

    @FXML
    private Label countLabel;

    @FXML
    private ListView<String> cardListView;

    @FXML
    public void initialize() {
        // Cartões adicionais emitidos para Jurema Queiroz
        var cards = FXCollections.observableArrayList(
                "Jurema Queiroz - Cartão Virtual - 01/01/2023",
                "Jurema Queiroz - Cartão Reserva - 15/03/2023",
                "Jurema Queiroz - Cartão Contactless - 08/07/2024"
        );

        cardListView.setItems(cards);

        // Mostra a quantidade total de cartões adicionais
        countLabel.setText("Total de cartões adicionais: " + cards.size());
    }

    @FXML
    public void handleNewAdditionalCardButton() {
        MainViewController.getInstance().setCenterView("additional-card-form-view.fxml");
    }

}
