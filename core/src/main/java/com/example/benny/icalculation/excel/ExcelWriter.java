package com.example.benny.icalculation.excel;


import com.example.benny.icalculation.core.LectureEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.temporal.IsoFields;
import java.util.*;

public class ExcelWriter {
    private static final Logger log = LogManager.getLogger(ExcelWriter.class);

    private XSSFWorkbook workbook;
    private ExcelReader reader;

    public ExcelWriter(File excelFile, List<LectureEvent> events) throws IOException, InvalidFormatException {
        log.error("File Space: {}", excelFile.length());

        log.info("Does file exist? {}", excelFile.exists());

        this.workbook = null;
        try (FileInputStream inputStream = new FileInputStream(excelFile.getPath())) {
            int available = inputStream.available();
            log.error("Available: {}", available);
            this.workbook = new XSSFWorkbook(inputStream);
        }
        reader = new ExcelReader(workbook);
    }

    static void main() throws IOException, InvalidFormatException {
        File excelFile = new File("test.xlsx");
        Path excelPath = excelFile.toPath();
        log.info(excelPath);

        byte[] bytes = Files.readAllBytes(excelFile.toPath());

        log.error(bytes.length);

        List<LectureEvent> events = new ArrayList<>();

        ZonedDateTime now = ZonedDateTime.now();

        events.add(new LectureEvent("ABC", now, now.plusHours(1)));
        events.add(new LectureEvent("ABC", now.plusHours(2), now.plusHours(3)));

        ExcelWriter writer = new ExcelWriter(excelFile, events);
        writer.appendToExcel(events);
    }

    public void appendToExcel(List<LectureEvent> events) throws IOException {
        XSSFSheet sheet = reader.getFirstSheet();
        List<ExcelRow> rows = Arrays.stream(reader.getInefficientRows(sheet)).toList();

        ExcelRow latestRow = rows.getLast();
        LocalDate latestLocalDate = latestRow.date();
        ZonedDateTime latestDate = latestLocalDate.atStartOfDay(ZoneId.of("Europe/Berlin"));

        int nextRowId = latestRow.rowId() + 1;

        CreationHelper creationHelper = workbook.getCreationHelper();
        CellStyle dateStyle = workbook.createCellStyle();

        dateStyle.setDataFormat(
                creationHelper.createDataFormat().getFormat("dd-mm-yyyy")
        );

        for (LectureEvent event : events) {
            ZonedDateTime  eventStartTime = event.getStartDate();
            if (eventStartTime.isBefore(latestDate)) { continue; }
            XSSFRow newRow = sheet.createRow(nextRowId);

            newRow.setRowStyle(reader.readRowStyleAt(nextRowId - 1, sheet));

            XSSFCell dateCell = newRow.createCell(0);

            dateCell.setCellValue(event.getStartDate().toLocalDate());

            dateCell.setCellStyle(dateStyle);

            nextRowId++;
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream("test.xlsx")) {
            workbook.write(fileOutputStream);
        }
    }
}
