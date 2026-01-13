package com.example.benny.icalculation.excel;


import com.example.benny.icalculation.core.LectureEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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

    public void appendToExcel(Path excelFile, List<LectureEvent> events) {
        try (FileInputStream inputStream = new FileInputStream(excelFile.toFile())) {

            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

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




        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
