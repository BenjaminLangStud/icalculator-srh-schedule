package com.example.benny.icalculation.excel;


import com.example.benny.icalculation.core.LectureEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.time.*;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalUnit;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ExcelWriter {
    private static final Logger log = LogManager.getLogger(ExcelWriter.class);

    private XSSFWorkbook workbook;
    private ExcelReader reader;
    private CellStyle dateStyle;
    private CellStyle startEndStyle;
    private CellStyle personStyle;
    private CellStyle formulaStyle;

    public ExcelWriter(File excelFile) throws IOException, InvalidFormatException {
        this.workbook = null;
        log.info("Starting to load workbook...");
        long startTime = System.nanoTime();
        try (FileInputStream inputStream = new FileInputStream(excelFile.getPath())) {
            this.workbook = new XSSFWorkbook(inputStream);
        }
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        double durationInMs = duration / Math.pow(10, 6);
        double durationInS = durationInMs / 1_000;
        double durationInMinutes = durationInS / 60;
        log.info("Finished loading workbook. Took {} milliseconds ({} seconds or {} minutes)", durationInMs, durationInS, durationInMinutes);
        reader = new ExcelReader(workbook);
    }

    static void main() throws IOException, InvalidFormatException {
        IOUtils.setByteArrayMaxOverride(150_000_000);

        File excelFile = new File("test.xlsx");
        ZonedDateTime now = ZonedDateTime.now();

        List<LectureEvent> events = new ArrayList<>();

        LocalTime startTime = LocalTime.of(9, 30, 0);
        LocalTime endTime = LocalTime.of(17, 30, 0);

        ZoneId zoneId = ZoneId.systemDefault();

        ZonedDateTime yesterday = LocalDate.now().minusDays(1).atStartOfDay(zoneId);
        ZonedDateTime today = LocalDate.now().atStartOfDay(zoneId);
        ZonedDateTime tomorrow = LocalDate.now().plusDays(1).atStartOfDay(zoneId);
        ZonedDateTime nextWeek = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).atStartOfDay(zoneId);

        LectureEvent nowEvent = new LectureEvent(
                "Now",
                today.with(startTime),
                today.with(endTime)
        );
        LectureEvent tomorrowEvent = new LectureEvent(
                "Tomorrow",
                tomorrow.with(startTime),
                tomorrow.with(endTime)
        );
        LectureEvent yesterdayEvent = new LectureEvent(
                "Yesterday",
                yesterday.with(startTime),
                yesterday.with(endTime)
        );

        LectureEvent nextWeekEvent = new LectureEvent(
                "Next Week",
                nextWeek.with(startTime),
                nextWeek.with(endTime)
        );

        events.add(nowEvent);
        events.add(tomorrowEvent);
        events.add(nextWeekEvent);
        events.add(yesterdayEvent);

        ExcelWriter writer = new ExcelWriter(excelFile);
        writer.appendToExcel(events);
    }

    public List<LectureEvent> stripLecturesAlreadyInExcelFile(List<LectureEvent> events, ExcelRow lastEventRow) {
        Iterator<LectureEvent> eventIterator = events.iterator();
        ZonedDateTime cutoffDate = lastEventRow.getDate().atStartOfDay(ZoneId.systemDefault());
        while (eventIterator.hasNext()) {
            LectureEvent event = eventIterator.next();

            ZonedDateTime startDate = event.getStartDate();

            if (startDate.isBefore(cutoffDate) || startDate.isEqual(cutoffDate))
                eventIterator.remove();
        }
        return events;
    }

    public void appendToExcel(List<LectureEvent> events) throws IOException {
        XSSFSheet sheet = reader.getFirstSheet();
        List<ExcelRow> rows = reader.getInefficientRows(sheet);

        ExcelRow latestRow = rows.getLast();
        LocalDate latestLocalDate = latestRow.getDate();

        events = stripLecturesAlreadyInExcelFile(events, latestRow);

        int currentLogicalRowId = latestRow.getRowId();

        CreationHelper creationHelper = workbook.getCreationHelper();
        XSSFRow firstDataRow = sheet.getRow(1);

        Cell firstDateCell = firstDataRow.getCell(0);
        log.info("First date cell: {}", firstDateCell);
        dateStyle = firstDateCell.getCellStyle();

        Cell firstStartTimeCell = firstDataRow.getCell(1);
        log.info("First start time cell: {}", firstStartTimeCell);
        startEndStyle = firstStartTimeCell.getCellStyle();

        Cell firstFormulaCell = firstDataRow.getCell(4);
        log.info("First formula cell: {}", firstFormulaCell);
        formulaStyle = firstFormulaCell.getCellStyle();

        int currentWeekOfYear = latestRow.getWeekOfYear();

        for (LectureEvent event : events) {
            currentLogicalRowId++;
            ExcelRow excelRow = new ExcelRow(currentLogicalRowId, event);
            if (excelRow.getWeekOfYear() > currentWeekOfYear) {
                currentLogicalRowId++;
                currentWeekOfYear = excelRow.getWeekOfYear();
                excelRow.setRowId(excelRow.getRowId() + 1);
            }
            XSSFRow row = excelRow.writeRow(sheet);
            addCommentToCell(sheet, row.getCell(0), event.getSummary());
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream("test-out.xlsx")) {
            workbook.write(fileOutputStream);
        }
    }

    private void addCommentToCell(Sheet sheet, Cell cell, String commentText) {
        CreationHelper factory = this.workbook.getCreationHelper();

        ClientAnchor anchor = factory.createClientAnchor();
        Row row = cell.getRow();

        anchor.setCol1(cell.getColumnIndex());
        anchor.setCol2(cell.getColumnIndex() + 3);
        anchor.setRow1(row.getRowNum());
        anchor.setRow2(row.getRowNum() + 1);

        Drawing<?> drawing = sheet.createDrawingPatriarch();
        Comment comment = drawing.createCellComment(anchor);
        comment.setString(factory.createRichTextString(commentText));
        comment.setAuthor("Me");

        cell.setCellComment(comment);
    }
}
