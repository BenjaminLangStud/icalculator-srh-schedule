package com.example.benny.icalculation.excel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.*;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class ExcelReader {
    private static final Logger log = LogManager.getLogger(ExcelReader.class);
    private XSSFWorkbook workbook;

    static void main() throws IOException {
        File testFile = new File("test.xlsx");

        ExcelReader reader = new ExcelReader(testFile);

        reader.readExcel();
    }

    public ExcelReader(File excelFile) throws IOException {
        IOUtils.setByteArrayMaxOverride(500_000_000);
        this.workbook = null;
        try (
                FileInputStream file = new FileInputStream(excelFile);
        ) {
            workbook = new XSSFWorkbook(file);
        }
    }

    public ExcelReader(XSSFWorkbook workbook) {
        this.workbook = workbook;
    }

    public void readExcel() {
        XSSFSheet sheet = getFirstSheet();

        log.info("Sheet: {}", sheet.getSheetName());

        List<ExcelRow> data = getInefficientRows(sheet);

        log.info("Length: {}", data.size());

        int numRowsNull = 0;

        for (ExcelRow row : data) {
            if (row == null) numRowsNull++;
            else numRowsNull = 0;
            System.out.println(row);
            if (numRowsNull >= 3) break;
        }
    }

    public List<ExcelRow> getInefficientRows(XSSFSheet sheet) {
        int lastRowNum = getRealLastRowNum(sheet);
        int columns = sheet.getRow(0).getLastCellNum();

        System.out.println("Last Row num: " + lastRowNum);

        List<ExcelRow> data = new ArrayList<>();

        Locale locale = Locale.GERMAN;

        Iterator<Row> rowIterator = sheet.rowIterator();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            int rowIndex = row.getRowNum();

            Cell dateCell = row.getCell(0);
            Cell startTimeCell = row.getCell(1);
            Cell endTimeCell = row.getCell(2);
            Cell personCell = row.getCell(3);
            Cell timeCell = row.getCell(4);
            LocalDate date = LocalDate.MIN;
            LocalTime start = LocalTime.MIDNIGHT;
            LocalTime end = LocalTime.MIDNIGHT;
            String person = "";

            if (dateCell == null) continue;

            ExcelRow excelRow = null;
            try {
                excelRow = parseExcelRowFromCells(dateCell, startTimeCell, endTimeCell, personCell, rowIndex);
            } catch (Exception e) {
                log.error("Could not parse: {} ({})", dateCell.toString(), e.toString());
            }
            if (excelRow != null)
                data.add(excelRow);
        }

        return data;
    }

    private @Nullable ExcelRow parseExcelRowFromCells(Cell dateCell, Cell startTime, Cell endTime, Cell personCell, int rowIndex) {
        Locale locale = Locale.ENGLISH;
        DataFormatter cellDataFormatter = new DataFormatter(locale);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", locale);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm", locale);

        String person = "";

        String dateString = cellDataFormatter.formatCellValue(dateCell);

        LocalDate date = null;
        LocalTime start = null;
        LocalTime end = null;
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

    private LocalDate parseDateFromString(String dateString, DateTimeFormatter formatter) {
//        log.info("Trying to parse \"{}\"", dateString);
        return LocalDate.parse(dateString, formatter);
    }

    public XSSFSheet getFirstSheet() {
        return getSheet(0);
    }

    private XSSFSheet getSheet(int sheetIndex) {
        return this.workbook.getSheetAt(sheetIndex);
    }

    public XSSFCellStyle readRowStyleAt(int rowIndex, XSSFSheet sheet) {
        XSSFRow sourceRow = sheet.getRow(rowIndex);
        return sourceRow.getRowStyle();
    }

    private static int getRealLastRowNum(Sheet sheet) {
        for (int i = sheet.getLastRowNum(); i >= 0; i--) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            for (Cell cell : row) {
                if (cell == null) continue;
                boolean isCellBlank = cell.getCellType() == CellType.BLANK;
                if (isCellBlank) { continue; }

                return i;
            }
        }
        return -1;
    }

    private static <T> T[] reverseArray(T[] data) {
        T[] copy = Arrays.copyOf(data, data.length);

        for (int i = 0; i < copy.length / 2; i++) {
            T temp = copy[i];
            copy[i] = copy[copy.length - i - 1];
            copy[copy.length - i - 1] = temp;
        }
        return copy;
    }

    private static int getIdOfLastRowWithData(ExcelRow[] data) {
        ExcelRow[] reversed = reverseArray(data);
        for (int i = 0; i < reversed.length; i++) {
            ExcelRow row = reversed[i];
            if (row.date() == null || row.date() == LocalDate.MIN) {
                return reversed.length - i;
            }
        }
        return 0;
    }

    private boolean isRowEmpty(Row row) {
        int lastCellId = row.getLastCellNum() - 1;
        return false;
    }
}
