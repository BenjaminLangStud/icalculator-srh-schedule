package com.benny.icalculation.gui;

import com.benny.icalculation.application.Caching.FileCacheService;
import com.benny.icalculation.application.Config;
import com.benny.icalculation.application.exceptions.ConfigIncompleteException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


public class MainSceneController {
    private static final Logger log = LogManager.getLogger(MainSceneController.class);
    Stage thisStage;

    public void setThisStage(Stage stage) { this.thisStage = stage; }

    private ToggleGroup outputToggleGroup;

    enum outputTypes {
        saveToFile,
        copyToClipboard
    }

    @FXML
    private TextField urlInput;

    @FXML
    private Button generateTextBtn;

    @FXML
    private Label generateTextLabel;

    @FXML
    private ChoiceBox<String> monthChoiceBox;

    @FXML
    private Label loadIcalFeedbackText;

    @FXML
    private CheckBox ignorePastCheckBox;

    @FXML
    private RadioButton radioButtonCopyClipboard;

    @FXML
    private RadioButton radioButtonSaveFile;

    void checkUIConfigs() {
        if (!(Config.getICalUri().compareTo(URI.create(urlInput.getText())) == 0)) {
            if (urlInput.getText().isBlank()) return;

            log.info("A new URL has been provided via UI");

            String newURL = urlInput.getText();
            URI uri = URI.create(newURL);

            final URL url;
            try {
                url = uri.toURL();
            } catch (MalformedURLException malformedURLException) {
                log.error("URL malformed");
                return;
            }
            Config.setICalUri(url.toString());
        }
    }

    @FXML
    private void OnClickTitleListener() {
        ProcessBuilder builder = new ProcessBuilder(
                "explorer.exe", Config.getAppDataDirectory().toString()
        );
        builder.redirectErrorStream(true);
        try {
            Process _ = builder.start();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @FXML
    private void generateTextBtnListener() {

        outputTypes outputType = (outputTypes) outputToggleGroup.getSelectedToggle().getUserData();

        checkUIConfigs();

        int stopAfterMonth = getMonthFromSelector();
        boolean ignorePast = ignorePastCheckBox.isSelected();

        log.info("Setting up DataProvider with: stopAfterMonth: {} and ignorePast: {}", stopAfterMonth, ignorePast);

        DataProvider service = new DataProvider(ignorePast, stopAfterMonth);

        generateTextLabel.textProperty().bind(service.messageProperty());

        service.setOnSucceeded(_ -> {
            String result = service.getValue();

            generateTextLabel.textProperty().unbind();
            generateTextLabel.textProperty().set("Done!");
            log.info("Result is here! It has {} letters!", result.length());

            if (outputType == outputTypes.copyToClipboard) {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(result);
                clipboard.setContent(content);
                log.info("Copied to clipboard.");

                showConfirmDialog("Copied to clipboard", "Paste it somewhere nice :)");

                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Text file", "txt"));
            fileChooser.setInitialDirectory(Path.of(".").toFile());
            fileChooser.setTitle("Output file");
            File file = fileChooser.showSaveDialog(thisStage);
            if (file == null) {
                log.error("No file was selected");
                return;
            }

            try (BufferedWriter bufferedWriter = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
                bufferedWriter.write(result);
            } catch (IOException exception) {
                log.info("File writing went wrong!");
            }
            log.info("result: {} [...]", result.substring(0, 10));

            showConfirmDialog("Written file", "It has been stored at " + file.toPath());
        });

        service.setOnFailed(_ -> {
            Throwable e = service.getException();
            generateTextLabel.textProperty().unbind();
            generateTextLabel.textProperty().set("Failed!");
            log.error("Task failed: {}", e.getMessage());
        });

        generateTextBtn.disableProperty().bind(service.runningProperty());

        log.info("Starting to load ical...");
        service.start();
    }

    void showConfirmDialog(String message, String submessage) {
        final Stage dialog = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(MainSceneController.class.getResource("confirmDialog.fxml")));
            Parent root = loader.load();
            DialogController controller = loader.getController();
            controller.setMessage(message, submessage);
            Scene scene = new Scene(root, 300, 200);
            dialog.setScene(scene);

            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.show();

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @FXML
    private void loadIcalButtonListener() {

        if (urlInput.getText().isBlank()) {
            showLoadIcalFeedback("No URL has been provided", true);
            return;
        }

        try {
            URL url = URI.create(urlInput.getText()).toURL();
            Config.setForceFetch(true);
            FileCacheService.getData();
        } catch (MalformedURLException e) {
            showLoadIcalFeedback("Malformed URL", true);
        } catch (IOException | InterruptedException e) {
            showLoadIcalFeedback("Something went wrong", true);
        }

        showLoadIcalFeedback("Success", false);
    }

    private void showLoadIcalFeedback(String message, boolean isBad) {
        String textFill = isBad ? "red" : "green";
        loadIcalFeedbackText.setText(message);
        loadIcalFeedbackText.setStyle("-fx-text-fill: " + textFill + ";");

        final KeyFrame kf1 = new KeyFrame(javafx.util.Duration.seconds(2), e -> {
            loadIcalFeedbackText.setText(message);
        });
        final KeyFrame kf2 = new KeyFrame(javafx.util.Duration.seconds(2), e -> {
            loadIcalFeedbackText.setText("");
        });
        final Timeline timeline = new Timeline(kf1, kf2);
        Platform.runLater(timeline::play);
    }

    void setupDataProvider() {
        Config.loadConfig();
    }

    int getMonthFromSelector() {
        String value = monthChoiceBox.getValue();
        return switch (value) {
            case "January" -> 1;
            case "February" -> 2;
            case "March" -> 3;
            case "April" -> 4;
            case "May" -> 5;
            case "June" -> 6;
            case "July" -> 7;
            case "August" -> 8;
            case "September" -> 9;
            case "October" -> 10;
            case "November" -> 11;
            case "December" -> 12;
            default -> -10;
        };
    }

    @FXML
    public void initialize() {

        setupDataProvider();

        generateTextBtn.onActionProperty().addListener(_ -> log.info("Generate btn was clicked!"));
//        generateTextBtn.onActionProperty().addListener(_ -> generateTextBtnListener());

        radioButtonSaveFile.setSelected(true);

        outputToggleGroup = new ToggleGroup();

        radioButtonSaveFile.setToggleGroup(outputToggleGroup);
        radioButtonCopyClipboard.setToggleGroup(outputToggleGroup);

        radioButtonSaveFile.setUserData(outputTypes.saveToFile);
        radioButtonCopyClipboard.setUserData(outputTypes.copyToClipboard);

        outputToggleGroup.selectedToggleProperty().addListener((observable, oldVal, newVal) -> log.info("{} was selected", newVal));

        monthChoiceBox.getItems().addAll("-- NONE --", "January", "February", "March", "April", "May", "June", "July", "August", "September","October", "November", "December");
        monthChoiceBox.getSelectionModel().select(0);

        makeInputDeselectable(urlInput);

        urlInput.focusedProperty().addListener((arg0, oldValue, newValue) -> {
            if (newValue) return;
            checkForURIInputValidity();
        });

        try {
            URI icalURL = Config.getICalUri();
            if (icalURL != null)
                urlInput.setText(icalURL.toString());
            checkForURIInputValidity();
        } catch (ConfigIncompleteException e) {
            log.error(e.getMessage());
        }

        log.info("Initialized MainScene");
    }

    void checkForURIInputValidity() {
        String inputtedText = urlInput.getText();

        if (inputtedText.isEmpty()) {
            urlInput.setStyle("-fx-text-fill: black;");
            return;
        }

        boolean isValidURL = true;

        try {
            URI.create(inputtedText).toURL();
        } catch (MalformedURLException | IllegalArgumentException e) {
            isValidURL = false;
        }

        if (isValidURL) {
            urlInput.getStyleClass().add("valid");
            urlInput.getStyleClass().remove("invalid");
        } else {
            urlInput.getStyleClass().add("invalid");
            urlInput.getStyleClass().remove("valid");
        }
    }

    private void makeInputDeselectable(Control control) {
        Set<KeyCode> deselectKeys = new HashSet<>();

        deselectKeys.add(KeyCode.ESCAPE);
        deselectKeys.add(KeyCode.ENTER);

//        control.setOnMouseClicked(event -> {
//            if (control.getBoundsInParent().contains(event.getX(), event.getY())) {
//                control.getParent().requestFocus();
//            }
//        });

        control.setOnKeyPressed(event -> {
            if (deselectKeys.contains(event.getCode())) {
                control.getParent().requestFocus();
            }
        });
    }
}
