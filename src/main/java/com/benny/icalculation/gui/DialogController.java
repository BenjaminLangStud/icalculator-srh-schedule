package com.benny.icalculation.gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.Random;

public class DialogController {
    String message = "ERROR";
    String subMessage = "ERROR";

    @FXML
    Label messageLabel;

    @FXML
    Label subMessageLabel;

    @FXML
    ImageView cringeEmoji;

    public DialogController() {
        this(null, null);
    }
    public DialogController(String message, String subMessage) {
        if (message != null)
            this.message = message;

        if (subMessage != null)
            this.subMessage = subMessage;
    }

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
            if (shouldShowCringeEmoji()) {
                cringeEmoji.setVisible(true);
            }
            this.messageLabel.setText(message);

            this.subMessageLabel.setText(Objects.requireNonNullElse(subMessage, ""));
        });
    }

    boolean shouldShowCringeEmoji() {
        Random random = new Random();
        boolean firstCoinFlip = random.nextBoolean();
        boolean secondCoinFlip = random.nextBoolean();

        return firstCoinFlip && secondCoinFlip;
    }
}
