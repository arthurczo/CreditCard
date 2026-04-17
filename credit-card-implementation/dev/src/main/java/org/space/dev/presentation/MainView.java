package org.jala.university.presentation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainView extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/login-view.fxml")); // Começa na tela de login
            Scene scene = new Scene(root, 500, 640);
//            Scene scene = new Scene(new Pane());

//        ViewSwitcher.setup(primaryStage, scene);
//        ViewSwitcher.switchTo(CreditCardView.MAIN.getView());

            scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());

            primaryStage.setScene(scene);
            primaryStage.setTitle("Credit Card Module Application");
            primaryStage.show();

        } catch (Exception e) {
            System.out.println("Erro ao carregar a tela de login.");
            e.printStackTrace();
        }
    }
}
