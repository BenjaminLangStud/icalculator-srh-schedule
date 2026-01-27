package com.example.benny.icalculation.excel;

import java.time.LocalDate;
import java.time.LocalTime;

public record ExcelRow (
        int rowId,
        LocalDate date,
        LocalTime start,
        LocalTime end,
        String person
) {
}
