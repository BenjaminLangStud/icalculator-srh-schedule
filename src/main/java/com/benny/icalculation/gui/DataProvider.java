package com.benny.icalculation.gui;

import com.benny.icalculation.application.LectureEvent;
import com.benny.icalculation.application.MainClass;
import com.benny.icalculation.application.formatting.TxtFormatter;
import javafx.beans.value.ObservableStringValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.fortuna.ical4j.data.ParserException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

public class DataProvider extends Service<String> {
    private static final Logger log = LogManager.getLogger(DataProvider.class);

    boolean ignorePast = false;
    int stopAfterMonth = -5;

    public DataProvider(boolean ignorePast, int stopAfterMonth) {
        this.ignorePast = ignorePast;
        this.stopAfterMonth = stopAfterMonth;
    }

    @Override
    protected Task<String> createTask() {
        return new Task<String>() {
            @Override
            protected String call() throws Exception {
                updateMessage("Loading...");

                String formatted = null;
                try {
                    List<LectureEvent> lectureEvents = MainClass.loadFromICal();
                    Collections.sort(lectureEvents);

                    formatted = TxtFormatter.formatEvents(lectureEvents);
                } catch (ParserException | IOException | InterruptedException e) {
                    formatted = "";
                    updateMessage("Error!");
                    log.error(e.getMessage());
                    return formatted;
                }

                updateMessage("Done!");

                return formatted;
            }
        };
    }
}
