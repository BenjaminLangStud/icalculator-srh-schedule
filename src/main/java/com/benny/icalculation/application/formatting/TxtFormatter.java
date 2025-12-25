package com.benny.icalculation.application.formatting;

import com.benny.icalculation.application.LectureEvent;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TxtFormatter {
    public static int LECTURE_PADDING_IN_MINUTES = 15;

    public static String formatEvents(List<LectureEvent> lectureEvents) {
        int currentWeekOfYear = 0;
        List<String> txtLines = new ArrayList<>();

        for (LectureEvent lecture : lectureEvents) {
            int weekOfYear = lecture.getStartDate().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);

            if (weekOfYear != currentWeekOfYear) {
                txtLines.add("");
                currentWeekOfYear = weekOfYear;
            }

            String oneLiner = formatEventToOneLiner(lecture);
            txtLines.add(oneLiner);
        }

        if (!txtLines.isEmpty()) {
            if (Objects.equals(txtLines.getFirst(), "")) {
                txtLines.removeFirst();
            }
        }

        return String.join("\n", txtLines);
    }

    public static String formatEventToOneLiner(LectureEvent event) {
        ZonedDateTime startDate = event.getStartDate();
        ZonedDateTime endDate = event.getEndDate();

        String datePart = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                .format(startDate.minusMinutes(LECTURE_PADDING_IN_MINUTES));

        String timeStartPart = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
                .format(startDate.minusMinutes(LECTURE_PADDING_IN_MINUTES));

        String timeEndPart = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
                .format(endDate.plusMinutes(LECTURE_PADDING_IN_MINUTES));

        return datePart + ": " + timeStartPart + "-" + timeEndPart;
    }
}
