package com.example.benny.icalculation.excel;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PrettyPrinter {

    private static final Logger log = LogManager.getLogger(PrettyPrinter.class);

    static void prettyTable(ExcelRow[] data) {
        prettyTable(data, 15);
    }

    static void prettyTable(ExcelRow[] data, int colWidth) {
        boolean headerRow = true;
        int columns = 3;

        log.info(data);
    }
}
