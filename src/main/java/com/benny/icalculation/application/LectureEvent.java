package com.benny.icalculation.application;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.ZonedDateTime;

/**
 * Holds data for a specific lecture
 */
public class LectureEvent implements Comparable<LectureEvent> {
    String summary;

    public LectureEvent(String summary, ZonedDateTime startDate, ZonedDateTime endDate) {
        this.summary = summary;
        this.startDate = startDate;
        this.endDate = endDate;
        this.duration = Duration.between(startDate, endDate);
    }

    /**
     * @return A Summary of this lecture
     */
    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * @hidden
     */
    public ZonedDateTime getStartDate() {
        return startDate;
    }

    /**
     * @hidden
     */
    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    ZonedDateTime startDate;
    ZonedDateTime endDate;


    public ZonedDateTime getEndDate() {
        return endDate;
    }

    /**
     * @hidden
     */
    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public Duration getDuration() {
        return duration;
    }

    Duration duration;

    /**
     * @return A human readable representation of this lectures duration in the form
     * <pre>
     * {hours} hours and {minutes} minutes
     * </pre>
     */
    public String getDurationAsString() {
        return duration.toHoursPart() + " hours and " + duration.toMinutesPart() + " minutes";
    }

    /**
     * Formats the given date.
     * By default, the format <code>dd.MM.yyyy HH:mm</code> is used.
     * <br><br>
     * Example:
     * <pre>{@code
     * LectureEvent.formatDate(new ZonedDateTime.now());
     * }</pre>
     *
     * @param date Date to format. Preferably as {@code ZonedDateTime}, because that is the only one tested.
     * @return the human-readable representation of the date
     */
    protected static String formatDate(ZonedDateTime date) {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        return df.format(java.util.Date.from(date.toInstant()));
    }

    @Override
    public String toString() {
        return "LectureEvent{" +
                "summary='" + summary + '\'' +
                ", startDate='" + formatDate(startDate) + '\'' +
                ", endDate='" + formatDate(endDate) + '\'' +
                ", duration='" + getDurationAsString() + '\'' +
                '}';
    }

    @Override
    public int compareTo(LectureEvent other) {
        return getStartDate().compareTo(other.getStartDate());
    }

    public boolean isOverlapping(LectureEvent other) {
        return this.startDate.isBefore(other.getEndDate()) && startDate.isBefore(this.getEndDate());
    }

    public boolean isLongerThan(LectureEvent other) {
        return this.getDuration().compareTo(other.getDuration()) > 0;
    }
}
