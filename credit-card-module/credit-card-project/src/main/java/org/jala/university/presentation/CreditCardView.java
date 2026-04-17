package org.jala.university.presentation;

import lombok.Getter;
import org.jala.university.commons.presentation.View;

@Getter
public enum CreditCardView {
    MAIN("credit-card-view.fxml"),
    TRANSACTIONS("transactions-view.fxml"),
    INVOICE("invoice-view.fxml"),
    BLOCK("views/block-view.fxml"),
    INCREASE_LIMIT("increase-limit-view.fxml");

    private final View view;

    CreditCardView(String fileName) {
        this.view = new View(fileName);
    }
}
