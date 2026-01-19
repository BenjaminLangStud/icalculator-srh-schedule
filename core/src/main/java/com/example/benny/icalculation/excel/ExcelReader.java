package com.example.benny.icalculation.excel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
        this.workbook = null;
        try (FileInputStream file = new FileInputStream(excelFile)) {
            workbook = new XSSFWorkbook(file);
        }
    }

    public ExcelReader(XSSFWorkbook workbook) {
        this.workbook = workbook;
    }

    public void readExcel() {
        XSSFSheet sheet = workbook.getSheetAt(0);

        log.info("Sheet: {}", sheet.getSheetName());

        int lastRowNum = getRealLastRowNum(sheet);
        int columns = sheet.getRow(0).getLastCellNum();

        System.out.println("Last Row num: " + lastRowNum);

        String[][] data = new String[lastRowNum + 1][columns];

        sheet.rowIterator().forEachRemaining(row -> {
            row.cellIterator().forEachRemaining(cell -> {
                int rowIndex = cell.getRowIndex();
                int columnIndex = cell.getColumnIndex();

                DataFormatter dataFormatter = new DataFormatter(Locale.GERMAN);

                switch (cell.getCellType()) {
                    case CellType.STRING -> data[rowIndex][columnIndex] = cell.getRichStringCellValue().getString();
                    case CellType.NUMERIC -> {
                        if (DateUtil.isCellDateFormatted(cell)) {
                            data[rowIndex][columnIndex] = dataFormatter.formatCellValue(cell);
                        } else {
                            data[rowIndex][columnIndex] = cell.getNumericCellValue() + "";
                        }
                    }
                    case CellType.BOOLEAN -> data[rowIndex][columnIndex] = cell.getBooleanCellValue() + "";
                    case CellType.FORMULA -> data[rowIndex][columnIndex] = cell.getCellFormula();
                    default -> data[rowIndex][columnIndex] = " ";
                }
            });
        });

        System.out.println("Last valid row: " + getIdOfLastRowWithData(data));

        PrettyPrinter.prettyTable(Arrays.copyOf(data, data.length), 14);
    }

    public TreeMap<ZonedDateTime,Row> getDateMap() {
        XSSFSheet sheet = workbook.getSheetAt(0);
        int lastRowNum = getRealLastRowNum(sheet);
        int cols = sheet.getRow(0).getLastCellNum();

        TreeMap<ZonedDateTime, Row> dateTimeRowMap = new TreeMap<>();

        for (int rowID = 0; rowID < lastRowNum; rowID++) {
            Row row = sheet.getRow(rowID);
            if (row == null) continue;

            Cell dateCell = row.getCell(0);
            if (Objects.requireNonNull(dateCell.getCellType()) != CellType.NUMERIC) continue;
            if (!DateUtil.isCellDateFormatted(dateCell)) continue;

            LocalDateTime date = dateCell.getLocalDateTimeCellValue();
            ZonedDateTime zonedDateTime = ZonedDateTime.of(date, ZoneId.of("Europe/Berlin"));

            dateTimeRowMap.put(zonedDateTime, row);
        }

        return dateTimeRowMap;
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

    private static int getIdOfLastRowWithData(String[][] data) {
        String[][] reversed = reverseArray(data);
        for (int i = 0; i < reversed.length; i++) {
            String[] row = reversed[i];
            if (row[0] == null || row[0].isEmpty()) {
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
