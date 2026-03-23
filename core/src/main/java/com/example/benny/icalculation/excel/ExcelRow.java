package com.example.benny.icalculation.excel;

import com.example.benny.icalculation.core.LectureEvent;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ExcelRow  {
    private int rowId;
    private LocalDate date;
    private LocalTime start;
    private LocalTime end;
    private String person;

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
        this.end = end;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public int getWeekOfYear() {
        return this.date.get(WeekFields.of(Locale.GERMANY).weekOfWeekBasedYear());
    }

    public ExcelRow(int rowId, LocalDate date, LocalTime start, LocalTime end, String person) {
        this.rowId = rowId;
        this.date = date;
        this.start = start;
        this.end = end;
        this.person = person;
    }

    public ExcelRow(int rowId, LectureEvent lectureEvent) {
        this.rowId = rowId;
        this.date = lectureEvent.getStartDate().toLocalDate();
        this.start = lectureEvent.getStartDate().toLocalTime();
        this.end = lectureEvent.getEndDate().toLocalTime();
        this.person = "";
    }

    public XSSFRow writeRow(XSSFSheet sheet) {
        XSSFRow newRow = sheet.createRow(this.rowId);

        XSSFCell dateCell = newRow.createCell(0);
        XSSFCell startCell = newRow.createCell(1);
        XSSFCell endCell = newRow.createCell(2);
        XSSFCell personCell = newRow.createCell(3);
        XSSFCell formulaCell = newRow.createCell(4);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss", Locale.GERMAN);
        TemporalUnit unit = TimeUnit.SECONDS.toChronoUnit();

        LocalTime localStartTime = this.start.truncatedTo(unit);
        LocalTime localEndTime = this.end.truncatedTo(unit);

        double excelStartTime = DateUtil.convertTime(localStartTime.toString());
        double excelEndTime = DateUtil.convertTime(localEndTime.toString());

        dateCell.setCellValue(this.date);

        startCell.setCellValue(excelStartTime);
        endCell.setCellValue(excelEndTime);

        int representableRowId = this.rowId + 1;
        String formula = "C" + representableRowId + "-B" + representableRowId;
        formulaCell.setCellFormula(formula);
        return newRow;
    }

    public static ExcelRow readFromRow(XSSFRow row) {
        int rowIndex = row.getRowNum();
        Cell dateCell = row.getCell(0);
        Cell startTime = row.getCell(1);
        Cell endTime = row.getCell(2);
        Cell personCell = row.getCell(3);

        Locale locale = Locale.ENGLISH;
        DataFormatter cellDataFormatter = new DataFormatter(locale);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", locale);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm", locale);

        String person = "";

        String dateString = cellDataFormatter.formatCellValue(dateCell);

        LocalDate date;
        LocalTime start;
        LocalTime end;
        try {
            date = parseDateFromString(dateString, dateFormatter);
            start = startTime.getLocalDateTimeCellValue().toLocalTime();
            end = endTime.getLocalDateTimeCellValue().toLocalTime();
        } catch (Exception e) {
            return null;
        }

        if (personCell != null) person = personCell.getStringCellValue();

        return new ExcelRow(rowIndex, date, start, end, person);
    }

    private static LocalDate parseDateFromString(String dateString, DateTimeFormatter formatter) {
        return LocalDate.parse(dateString, formatter);
    }
}
