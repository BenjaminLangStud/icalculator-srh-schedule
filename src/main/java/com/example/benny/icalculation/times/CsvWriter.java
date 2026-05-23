package com.example.benny.icalculation.times;

import java.io.File;

public class CsvWriter {
    public File getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    private File outputFile = new File("times.csv");

    public CsvWriter(String outputFile) {
        setOutputFile(new File(outputFile));
    }
}
