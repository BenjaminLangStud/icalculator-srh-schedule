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
            log.debug(row);
            if (numRowsNull >= 3) break;
        }
    }

    public List<ExcelRow> getInefficientRows(XSSFSheet sheet) {
        int lastRowNum = getRealLastRowNum(sheet);

        log.info("Last Row num: {}", lastRowNum);

        List<ExcelRow> data = new ArrayList<>();
        Iterator<Row> rowIterator = sheet.rowIterator();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Cell dateCell = row.getCell(0);

            if (dateCell == null) continue;

            ExcelRow excelRow = null;
            try {
                excelRow = ExcelRow.readFromRow((XSSFRow) row);
            } catch (Exception e) {
                log.error("Could not parse: {} ({})", dateCell.toString(), e.toString());
            }

            if (excelRow != null)
                data.add(excelRow);
        }

        return data;
    }

    public XSSFSheet getFirstSheet() {
        return getSheet(0);
    }

    private XSSFSheet getSheet(int sheetIndex) {
        return this.workbook.getSheetAt(sheetIndex);
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
}
