package com.benny.icalculation.application;

import com.benny.icalculation.application.Caching.FileCacheService;
import com.benny.icalculation.application.formatting.TxtFormatter;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.VEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Application entry point
 */
public class MainClass {

    static final Logger logger = LogManager.getLogger(MainClass.class);

    static void main() throws IOException, InterruptedException, ParserException {

        logger.info("Startup");

        Config.loadConfig();

        List<LectureEvent> lectureEvents = loadFromICal();

        Collections.sort(lectureEvents);

        new TxtWriter.Builder()
                .lectureList(lectureEvents)
                .ignorePast()
                .stopAfter().february()
                .build().writeToFile();
    }

    /**
     * Loads all LectureEvents from the ical
     * @return All LectureEvents, either from cache or freshly from the internet
     */
    public static List<LectureEvent> loadFromICal() throws ParserException, IOException, InterruptedException {
        CalendarBuilder builder = new CalendarBuilder();
        String iCalData = FileCacheService.getData();

        Calendar calendar = builder.build(new StringReader(iCalData));

        List<LectureEvent> lectureEvents = new ArrayList<>();
        for (Component component : calendar.getComponentList().getAll()) {
            if (component instanceof VEvent event) {
                LectureEvent lectureEvent = parseEvent(event);
                lectureEvents.add(lectureEvent);
            }
        }
        return lectureEvents;
    }

    public static LectureEvent parseEvent(VEvent event) {
        String summary = event.getSummary().getValue();
        Temporal startDate = event.getDateTimeStart().getDate();
        Temporal endDate = event.getDateTimeEnd().getDate();

        return new LectureEvent(summary, (ZonedDateTime) startDate, (ZonedDateTime) endDate);
    }
}
