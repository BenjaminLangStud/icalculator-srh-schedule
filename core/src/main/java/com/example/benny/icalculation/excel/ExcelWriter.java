package com.example.benny.icalculation.excel;


import com.example.benny.icalculation.core.LectureEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.TreeMap;
import java.util.stream.Stream;

public class ExcelWriter {
    private static final Logger log = LogManager.getLogger(ExcelWriter.class);

    static void main() {
        File excelFile = new File("test.xlsx");

        ExcelWriter writer = new ExcelWriter();
        List<LectureEvent> events  = new ArrayList<>();

        ZonedDateTime now = ZonedDateTime.now();

        events.add(new LectureEvent("ABC", now, now.plusHours(1)));
        events.add(new LectureEvent("ABC", now.plusHours(2), now.plusHours(3)));

        writer.appendToExcel(excelFile, events);
    }

    public void appendToExcel(File excelFile, List<LectureEvent> events) {
        try (FileInputStream inputStream = new FileInputStream(excelFile)) {

            XSSFWorkbook workbook = new XSSFWorkbook(excelFile);

            Sheet sheet = workbook.getSheetAt(0);

            ExcelReader reader = new ExcelReader(workbook);

            TreeMap<ZonedDateTime, Row> dateMap = reader.getDateMap();

            Stack<LectureEvent> lectureEventStack = new Stack<>();

            int lastWeekOfYear = 0;

            for (LectureEvent lectureEvent : events) {
                if (!dateMap.containsKey(lectureEvent.getStartDate())) {
                    lectureEventStack.add(lectureEvent);
                    int weekOfYear = lectureEvent.getStartDate().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);

                    if (weekOfYear > lastWeekOfYear) {
                        lectureEventStack.push(null);
                    }
                }
            }

            log.info(lectureEventStack.size());

        } catch (IOException | InvalidFormatException e) {
            throw new RuntimeException(e);
        }
    }
}
