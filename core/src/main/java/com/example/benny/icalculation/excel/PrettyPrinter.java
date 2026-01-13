package com.example.benny.icalculation.excel;

import org.apache.commons.lang3.StringUtils;

public class PrettyPrinter {

    static void prettyTable(String[][] data) {
        prettyTable(data, 15);
    }

    static void prettyTable(String[][] data, int colWidth) {
        boolean headerRow = true;
        int columns = data[0].length;

        for (String[] row : data) {
            System.out.print("|");
            for (String cell : row) {
                if (cell == null) cell = "";
                System.out.print(StringUtils.center(cell, colWidth) + "|");
            }
            System.out.println();

            if (headerRow) {
                String filler_column = " " + String.format("%0" + (colWidth - 2) + "d", 0).replace("0", "-") + " |";

                String columnRow = String.format("%0" + columns + "d", 0).replace("0", filler_column);

                System.out.println("|" + columnRow);
                headerRow = false;
            }
        }
    }
}
