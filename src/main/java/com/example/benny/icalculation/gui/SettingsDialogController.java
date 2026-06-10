package com.example.benny.icalculation.gui;

import javafx.fxml.FXML;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SettingsDialogController {
    private static final Logger log = LogManager.getLogger(SettingsDialogController.class);

    @FXML
    private void initialize() {
        log.info("Settings Dialog initialized");
    }
}
