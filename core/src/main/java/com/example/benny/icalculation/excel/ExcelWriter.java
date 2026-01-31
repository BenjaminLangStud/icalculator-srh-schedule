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
import java.time.format.DateTimeFormatter;
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

    public ExcelWriter(File excelFile, List<LectureEvent> events) throws IOException, InvalidFormatException {
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
        IOUtils.setByteArrayMaxOverride(500_000_000);

        File excelFile = new File("test.xlsx");
        ZonedDateTime now = ZonedDateTime.now();

        List<LectureEvent> events = new ArrayList<>();

        LocalTime startTime = LocalTime.of(9, 30, 0);
        LocalTime endTime = LocalTime.of(17, 30, 0);

        LectureEvent nowEvent = new LectureEvent(
                "Now",
                LocalDate.now().atTime(startTime).atZone(ZoneId.systemDefault()),
                LocalDate.now().atTime(endTime).atZone(ZoneId.systemDefault())
        );

        LectureEvent nextWeekEvent = new LectureEvent(
                "Next Week",
                LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).atTime(startTime).atZone(ZoneId.systemDefault()),
                LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).atTime(endTime).atZone(ZoneId.systemDefault())
        );

        events.add(nowEvent);
        events.add(nextWeekEvent);

        ExcelWriter writer = new ExcelWriter(excelFile, events);
        writer.appendToExcel(events);
    }

    public void appendToExcel(List<LectureEvent> events) throws IOException {
        XSSFSheet sheet = reader.getFirstSheet();
        List<ExcelRow> rows = reader.getInefficientRows(sheet);

        ExcelRow latestRow = rows.getLast();
        LocalDate latestLocalDate = latestRow.date();
        ZonedDateTime latestDate = latestLocalDate.atStartOfDay(ZoneId.of("Europe/Berlin"));

        int nextLogicalRowId = latestRow.rowId() + 1;

        CreationHelper creationHelper = workbook.getCreationHelper();
        XSSFRow firstDataRow = sheet.getRow(1);

        Cell firstDateCell = firstDataRow.getCell(0);
        System.out.println(firstDateCell.toString());
        dateStyle = firstDateCell.getCellStyle();

        Cell firstStartTimeCell = firstDataRow.getCell(1);
        System.out.println(firstStartTimeCell);
        startEndStyle = firstStartTimeCell.getCellStyle();

        Cell firstFormulaCell = firstDataRow.getCell(4);
        System.out.println(firstFormulaCell);
        formulaStyle = firstFormulaCell.getCellStyle();

//        this.dateStyle.setDataFormat(
//                creationHelper.createDataFormat().getFormat("dd-mm-yyyy")
//        );


        for (LectureEvent event : events) {
            createNextRow(event, sheet, nextLogicalRowId);
            nextLogicalRowId++;
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream("test-out.xlsx")) {
            workbook.write(fileOutputStream);
        }
    }

    private static int getWeekOfYearFromLocalDate(LocalDate date) {
        WeekFields weekFields = WeekFields.of(Locale.GERMANY);
        return date.get(weekFields.weekOfWeekBasedYear());
    }

    private void createNextRow(LectureEvent event, XSSFSheet sheet, int logicalRowId) {
        XSSFRow newRow = sheet.createRow(logicalRowId);

        XSSFCellStyle lastCellStyle = reader.readRowStyleAt(logicalRowId - 1, sheet);
        if (lastCellStyle != null)
            log.info("Cell style: {}", lastCellStyle.getDataFormatString());
        else
            log.warn("Last cell style is null");

        XSSFCell dateCell = newRow.createCell(0);
        XSSFCell startCell = newRow.createCell(1);
        XSSFCell endCell = newRow.createCell(2);
        XSSFCell personCell = newRow.createCell(3);
        XSSFCell formulaCell = newRow.createCell(4);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss", Locale.GERMAN);
        TemporalUnit unit = TimeUnit.SECONDS.toChronoUnit();

        LocalTime localStartTime = event.getStartDate().toLocalTime().truncatedTo(unit);
        LocalTime localEndTime = event.getEndDate().toLocalTime().truncatedTo(unit);

        double excelStartTime = DateUtil.convertTime(localStartTime.toString());
        double excelEndTime = DateUtil.convertTime(localEndTime.toString());

        dateCell.setCellValue(event.getStartDate().toLocalDate());
//        startCell.setCellValue(startTime);
//        endCell.setCellValue(endTime);

        startCell.setCellValue(excelStartTime);
        endCell.setCellValue(excelEndTime);

        int representableRowId = logicalRowId + 1;
        String formula = "C" + representableRowId + "-B" + representableRowId;
        formulaCell.setCellFormula(formula);

        dateCell.setCellStyle(dateStyle);
        startCell.setCellStyle(startEndStyle);
        endCell.setCellStyle(startEndStyle);
//        personCell.setCellStyle(personStyle);
        formulaCell.setCellStyle(formulaStyle);
    }
}
