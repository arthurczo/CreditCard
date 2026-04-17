package org.jala.university.presentation.controller;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class AdditionalCardFormController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField cpfField;

    @FXML
    private DatePicker birthDatePicker;

    @FXML
    private void handleSubmit() {
        String name = nameField.getText();
        String cpf = cpfField.getText();
        String birthDate = (birthDatePicker.getValue() != null) ? birthDatePicker.getValue().toString() : "";

        System.out.println("Solicitação enviada:");
        System.out.println("Nome: " + name);
        System.out.println("CPF: " + cpf);
        System.out.println("Data de nascimento: " + birthDate);

        // Aqui dá pra salvar as informações ou avisar o usuário que deu certo.
    }
}
