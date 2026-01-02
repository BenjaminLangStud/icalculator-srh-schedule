package com.benny.icalculation.application;


import com.benny.icalculation.application.formatting.LectureSorter;
import com.benny.icalculation.application.formatting.TxtFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TxtWriter {
    private static final Logger log = LogManager.getLogger(TxtWriter.class);
    List<LectureEvent> lectureEventList;
    public List<LectureEvent> lecturesToUse = new ArrayList<>();
    boolean ignorePastLectures;
    boolean ignoreOverlap;
    int monthMax;

    /**
     * @param lectureEventList The lectures for which to create the report
     * @param ignorePastLectures Wether lectures in the past should be left out of the output
     * @param monthMax up to which month (1-12) should the report be created? -1 for no limit. The month is inclusive, it will be the last month represented in the output
     */
    public TxtWriter(
            List<LectureEvent> lectureEventList,
            boolean ignorePastLectures,
            int monthMax,
            boolean ignoreOverlap
    ) {
        this.lectureEventList = lectureEventList;
        this.ignorePastLectures = ignorePastLectures;
        this.monthMax = monthMax;
        this.ignoreOverlap = ignoreOverlap;
    }

    public TxtWriter() {

    }

    /**
     * @return True on success, false on failure
     *
     * @deprecated Use {@code  TxtWriter.prepare(LectureSorter sorter)} instead
     */
    public boolean prepare() {
        LectureSorter lectureSorter = new LectureSorter(this.lectureEventList, this.monthMax, this.ignoreOverlap, this.ignorePastLectures);
        return this.prepare(lectureSorter);
    }

    public boolean prepare(LectureSorter sorter) {
        this.lecturesToUse = sorter.sort();
        return !this.lecturesToUse.isEmpty();
    }

    public void writeToFile() { writeToFile(Config.outputFile); }

    public void writeToFile(String outFile) {
        if (lecturesToUse.isEmpty()) {
            if (!prepare()) {
                log.error("There are no lectures to write to the output!");
                return;
            }
        }

        int lecturesIgnored = this.lectureEventList.size() - this.lecturesToUse.size();
        if (this.lectureEventList.size() > this.lecturesToUse.size()) {
            log.info("{} lectures have been ignored.", lecturesIgnored);
        }

        try (FileWriter writer = new FileWriter(outFile)) {
            String toWrite = TxtFormatter.formatEvents(lecturesToUse);

            writer.write(toWrite);
            System.out.println("Written output to file " + outFile + ".");
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
        }
    }

    public static class Builder {
        List<LectureEvent> lectureEventList = new ArrayList<>();
        boolean ignorePastLectures = false;
        int monthMax = -1;
        boolean ignoreOverlap = false;

        public Builder lectureList(List<LectureEvent> lectureEventList) {
            this.lectureEventList = lectureEventList;
            return this;
        }

        public Builder ignorePast() {
            this.ignorePastLectures = true;
            return this;
        }

        //<editor-fold desc="stop after month methods">

        public MonthSelector stopAfter() {
            return new MonthSelector(this);
        }

        protected Builder stopAfterMonth(int month) {
            this.monthMax = month;
            return this;
        }

        //</editor-fold>

        public Builder ignoreOverlap() { this.ignoreOverlap = true; return this; }

        public TxtWriter build() {
            return new TxtWriter(lectureEventList, ignorePastLectures, monthMax, ignoreOverlap);
        }

        public static class MonthSelector {
            private final Builder builder;

            MonthSelector(Builder builder) { this.builder = builder; }

            public Builder january() { return builder.stopAfterMonth(1); }
            public Builder february() { return builder.stopAfterMonth(2); }
            public Builder march() { return builder.stopAfterMonth(3); }
            public Builder april() { return builder.stopAfterMonth(4); }
            public Builder may() { return builder.stopAfterMonth(5); }
            public Builder june() { return builder.stopAfterMonth(6); }
            public Builder july() { return builder.stopAfterMonth(7); }
            public Builder august() { return builder.stopAfterMonth(8); }
            public Builder september() { return builder.stopAfterMonth(9); }
            public Builder october() { return builder.stopAfterMonth(10); }
            public Builder november() { return builder.stopAfterMonth(11); }
            public Builder december() { return builder.stopAfterMonth(12); }
        }
    }
}
