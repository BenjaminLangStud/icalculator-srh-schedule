package com.benny.icalculation.application.formatting;

import com.benny.icalculation.application.LectureEvent;
import com.benny.icalculation.application.TxtWriter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LectureSorter {

    static ZonedDateTime today = new Date().toInstant().atZone(ZoneId.systemDefault());
    List<LectureEvent> sortedLectureEvents = new ArrayList<>();
    List<LectureEvent> allLectureEvents;
    int monthMax = -1;
    boolean ignoreOverlap = true;
    boolean ignorePastLectures = true;



    public LectureSorter(List<LectureEvent> lectures, int monthMax, boolean ignoreOverlap, boolean ignorePastLectures) {
        this.allLectureEvents = lectures;
        this.monthMax = monthMax;
        this.ignoreOverlap = ignoreOverlap;
        this.ignorePastLectures = ignorePastLectures;
    }

    public List<LectureEvent> sort() {
        if (this.allLectureEvents.isEmpty()) {
            return sortedLectureEvents;
        }

        final int biggestValidMonth = 12;

        if (monthMax < 1) {
            monthMax = biggestValidMonth + 1;
        }

//        System.out.println(this.allLectureEvents);

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

        if (ignorePastLectures && startDate.isBefore(today))
            return false;

        return true;
    }

    public static class Builder {
        List<LectureEvent> allLectureEvents;
        int monthMax = -1;
        boolean ignoreOverlap = false;
        boolean ignorePastLectures = false;

        public Builder lectureEvents(List<LectureEvent> lectureEvents) {
            this.allLectureEvents = lectureEvents;
            return this;
        }

        public Builder ignorePast() { return this.ignorePast(true); }
        public Builder ignorePast(boolean ignorePast) {
            this.ignorePastLectures = true;
            return this;
        }

        public Builder ignoreOverlap() { return this.ignoreOverlap(true); }
        public Builder ignoreOverlap(boolean ignoreOverlap) {
            this.ignoreOverlap = ignoreOverlap;
            return this;
        }

        public MonthSelector stopAfter() { return new MonthSelector(this); }

        public Builder stopAfterMonth(int month) {
            this.monthMax = month;
            return this;
        }

        public LectureSorter build() {
            return new LectureSorter(this.allLectureEvents, this.monthMax, this.ignoreOverlap, this.ignorePastLectures);
        }

        public static class MonthSelector {
            private final LectureSorter.Builder builder;

            MonthSelector(LectureSorter.Builder builder) { this.builder = builder; }

            public LectureSorter.Builder january() { return builder.stopAfterMonth(1); }
            public LectureSorter.Builder february() { return builder.stopAfterMonth(2); }
            public LectureSorter.Builder march() { return builder.stopAfterMonth(3); }
            public LectureSorter.Builder april() { return builder.stopAfterMonth(4); }
            public LectureSorter.Builder may() { return builder.stopAfterMonth(5); }
            public LectureSorter.Builder june() { return builder.stopAfterMonth(6); }
            public LectureSorter.Builder july() { return builder.stopAfterMonth(7); }
            public LectureSorter.Builder august() { return builder.stopAfterMonth(8); }
            public LectureSorter.Builder september() { return builder.stopAfterMonth(9); }
            public LectureSorter.Builder october() { return builder.stopAfterMonth(10); }
            public LectureSorter.Builder november() { return builder.stopAfterMonth(11); }
            public LectureSorter.Builder december() { return builder.stopAfterMonth(12); }
        }
    }
}
