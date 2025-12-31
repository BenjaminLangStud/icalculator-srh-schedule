package com.benny.icalculation.gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.Objects;

public class DialogController {
    String message = "ERROR";
    String subMessage = "ERROR";

    @FXML
    Label messageLabel;

    @FXML
    Label subMessageLabel;

    public  void setMessage(String message) {
        this.setMessage(message, null);
    }
    public void setMessage(String message, String subMessage) {
        this.message = message;
        this.subMessage = subMessage;
    }

    @FXML
    void okBtnHandler(ActionEvent event) {
        Button button = (Button) event.getSource();
        ((Stage) button.getScene().getWindow()).close();
    }

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            this.messageLabel.setText(message);

            this.subMessageLabel.setText(Objects.requireNonNullElse(subMessage, ""));
        });
    }
}
