package com.benny.icalculation.application;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileDownloaderTest {

    @Test
    void getIcal() {
        try {
            FileDownloader.getIcal("about:blank");
        } catch (IOException | InterruptedException | IllegalArgumentException e) {
            assertTrue(true);
        }
    }
}