package com.example.benny.icalculation.gui;

import com.example.benny.icalculation.core.LectureEvent;
import com.example.benny.icalculation.core.MainClass;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.fortuna.ical4j.data.ParserException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataFetchingService extends Service<List<LectureEvent>> {
    private static final Logger log = LogManager.getLogger(DataFetchingService.class);

    @Override
    protected Task<List<LectureEvent>> createTask() {
        return new Task<List<LectureEvent>>() {
            @Override
            protected List<LectureEvent> call() {
                updateMessage("Loading...");

                List<LectureEvent> lectureEvents;
                try {
                    lectureEvents = MainClass.loadFromICal();
                    Collections.sort(lectureEvents);
                    updateMessage("Done!");
                    return lectureEvents;
                } catch (ParserException | IOException | InterruptedException e) {
                    updateMessage("Error!");
                    log.error(e.getMessage());
                    updateMessage("Failed");
                    Thread.currentThread().interrupt();
                    return Collections.emptyList();
                }
            }
        };
    }
}
