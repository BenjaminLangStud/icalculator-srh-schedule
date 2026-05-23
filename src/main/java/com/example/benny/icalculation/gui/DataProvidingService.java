package com.example.benny.icalculation.gui;

import com.example.benny.icalculation.core.LectureEvent;
import com.example.benny.icalculation.core.MainClass;
import com.example.benny.icalculation.core.TxtWriter;
import com.example.benny.icalculation.core.formatting.TxtFormatter;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.fortuna.ical4j.data.ParserException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class DataProvidingService extends Service<String> {
    private static final Logger log = LogManager.getLogger(DataProvidingService.class);
    private List<LectureEvent> lectureEvents;

    boolean ignorePast = false;
    int stopAfterMonth = -5;
    List<String> ignoredLectures;

    public boolean isIgnorePast() {
        return ignorePast;
    }

    public void setIgnorePast(boolean ignorePast) {
        this.ignorePast = ignorePast;
    }

    public int getStopAfterMonth() {
        return stopAfterMonth;
    }

    public void setStopAfterMonth(int stopAfterMonth) {
        this.stopAfterMonth = stopAfterMonth;
    }

    public List<String> getIgnoredLectures() {
        return ignoredLectures;
    }

    public void setIgnoredLectures(List<String> ignoredLectures) {
        this.ignoredLectures = ignoredLectures;
    }

    public DataProvidingService() {}

    public DataProvidingService(boolean ignorePast, int stopAfterMonth, List<String> ignoredLectures, List<LectureEvent> lectureEvents) {
        this.ignorePast = ignorePast;
        this.stopAfterMonth = stopAfterMonth;
        this.ignoredLectures = ignoredLectures;
        this.lectureEvents = lectureEvents;
    }

    @Override
    protected Task<String> createTask() {
        return new Task<String>() {
            @Override
            protected String call() {
                if (lectureEvents.isEmpty()) {
                    updateMessage("Error: Events not loaded");
                    log.warn("Lecture Events are empty");
                    return null;
                }

                updateMessage("Loading...");

                StringBuilder formatted = new StringBuilder();

                TxtWriter writer = new TxtWriter(lectureEvents, ignorePast, stopAfterMonth, true, ignoredLectures);
                writer.prepare();
                formatted.append(TxtFormatter.formatEvents(writer.lecturesToUse));

                updateMessage("Done!");
                log.info("Event formatting done");

                return formatted.toString();
            }
        };
    }
}
