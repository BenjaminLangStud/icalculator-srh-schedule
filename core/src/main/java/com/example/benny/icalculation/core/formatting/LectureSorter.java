package com.example.benny.icalculation.core.formatting;

import com.example.benny.icalculation.core.LectureEvent;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class LectureSorter {

    private final List<LectureEvent> allLectureEvents;
    private final List<LectureEvent> sortedLectureEvents = new ArrayList<>();
    private int monthMax = -1;
    private static final ZonedDateTime today = new Date().toInstant().atZone(ZoneId.systemDefault());
    private boolean ignoreOverlap = true;
    private boolean ignorePastLectures = true;
    private List<String> ignoredLecutres;



    public LectureSorter(List<LectureEvent> lectures, int monthMax, boolean ignoreOverlap, boolean ignorePastLectures, List<String> ignoredLecutres) {
        this.allLectureEvents = lectures;
        this.monthMax = monthMax;
        this.ignoreOverlap = ignoreOverlap;
        this.ignorePastLectures = ignorePastLectures;
        this.ignoredLecutres = ignoredLecutres;
    }

    public List<LectureEvent> sort() {
        if (this.allLectureEvents.isEmpty()) {
            return sortedLectureEvents;
        }

        final int biggestValidMonth = 12;

        if (monthMax < 1) {
            monthMax = biggestValidMonth + 1;
        }

        for (LectureEvent lectureEvent : allLectureEvents) {
            if (shouldLectureBeIncluded(lectureEvent))
                sortedLectureEvents.add(lectureEvent);
        }

        return sortedLectureEvents;
    }

    /**
     * Validates if a lectureEvent should be included in the output according to the flags set in this class
     * @param lectureEvent The lecture in question
     * @return the lectureEvent or null, if it should not be included
     */
    private boolean shouldLectureBeIncluded(LectureEvent lectureEvent) {
        ZonedDateTime startDate = lectureEvent.getStartDate();
        if (startDate.getMonthValue() > monthMax) return false;

        boolean isOverlapping = false;
        boolean isFirstLecture = this.sortedLectureEvents.isEmpty();
        if (!isFirstLecture) {
            isOverlapping = lectureEvent.isOverlapping(sortedLectureEvents.getLast());
        }

        if (ignoreOverlap && isOverlapping) {
            if (this.sortedLectureEvents.getLast().isLongerThan(lectureEvent)) {
                return false;
            }
            this.sortedLectureEvents.removeLast();
        }

        if (!ignoredLecutres.isEmpty() && ignoredLecutres.contains(lectureEvent.getSummary())) {
            return false;
        }

        //noinspection RedundantIfStatement
        if (ignorePastLectures && startDate.isBefore(today)) {
            return false;
        }

        return true;
    }
}
