package com.benny.icalculation.application;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

class LectureEventTest {
    String summary;
    ZonedDateTime dtStart, dtEnd;
    LectureEvent event;

    @BeforeEach
    public void setUp() {
        dtStart = ZonedDateTime.of(2025, 12, 22, 11, 53, 0, 0, ZoneId.of("Europe/Berlin"));
        dtEnd = dtStart.plusHours(2);
        summary = "Summary";

        event = new LectureEvent(summary, dtStart, dtEnd);
    }

    @org.junit.jupiter.api.Test
    void getDurationAsString() {
        LectureEvent event = new LectureEvent(summary, dtStart, dtEnd);

        String resultString = event.getDurationAsString();

        assertEquals("2 hours and 0 minutes", resultString);
    }

    @Test
    @DisplayName("Format date")
    void formatDate() {
        ZonedDateTime date = ZonedDateTime.of(2025, 12, 22, 11, 53, 0, 0, ZoneId.of("Europe/Berlin"));

        assertEquals("22.12.2025 11:53", LectureEvent.formatDate(date));
    }

    @Test
    void getSummary() {
    }

    @Test
    void setSummary() throws NoSuchFieldException, IllegalAccessException {
        event.setSummary("abc");

        final Field field = event.getClass().getDeclaredField("summary");
        field.setAccessible(true);
        assertEquals("abc", field.get(event));
    }

    @Test
    void getStartDate() {
    }

    @Test
    void setStartDate() throws NoSuchFieldException, IllegalAccessException {
        ZonedDateTime newDate = dtStart.minusHours(2);

        event.setStartDate(newDate);

        final Field field = event.getClass().getDeclaredField("startDate");
        field.setAccessible(true);
        assertEquals(newDate, field.get(event));
    }

    @Test
    void getEndDate() {
    }

    @Test
    void setEndDate() throws NoSuchFieldException, IllegalAccessException {
        ZonedDateTime newDate = dtEnd.plusHours(2);

        event.setEndDate(newDate);

        final Field field = event.getClass().getDeclaredField("endDate");
        field.setAccessible(true);
        assertEquals(newDate, field.get(event));
    }

    @Test
    void getDuration() throws NoSuchFieldException, IllegalAccessException {

        final Field field = event.getClass().getDeclaredField("duration");
        field.setAccessible(true);
        Duration actualDuration = (Duration) field.get(event);

        Duration expectedDuration = Duration.ofHours(2);

        assertEquals(expectedDuration, actualDuration);
        assertEquals(expectedDuration, event.getDuration());
    }

    @Test
    void testGetDurationAsString() throws NoSuchFieldException, IllegalAccessException {
        final Duration actualDuration = Duration.ofMinutes(100);

        final Field field = event.getClass().getDeclaredField("duration");
        field.setAccessible(true);
        field.set(event, actualDuration);

        String resultString = event.getDurationAsString();
        String expectedString = actualDuration.toHoursPart() + " hours and " + actualDuration.toMinutesPart() + " minutes";

        assertEquals(expectedString, resultString);
    }

    @Test
    void testFormatDate() {
//        String resultString = LectureEvent.formatDate(dtStart);
//        assertEquals("22.12.2025 11:53", resultString);
    }

    @Test
    void testToString() {
//        String expectedString = "LectureEvent{summary='Summary', startDate='22.12.2025 11:53', endDate='22.12.2025 13:53', duration='2 hours and 0 minutes'}";
//        String resultString = event.toString();
//
//        assertEquals(expectedString, resultString);
    }

    @Test
    void testGetSummary() {
    }

    @Test
    void testSetSummary() {
    }

    @Test
    void testSetStartDate() {
    }

    @Test
    void testGetEndDate() throws NoSuchFieldException, IllegalAccessException {
        final Field field = event.getClass().getDeclaredField("endDate");
        field.setAccessible(true);
        Temporal actualEndDate = (Temporal) field.get(event);

        Temporal expectedEndDate = event.getEndDate();

        assertEquals(expectedEndDate, actualEndDate);
    }

    @Test
    void testSetEndDate() {
    }

    @Test
    void testGetDuration() {
    }
}