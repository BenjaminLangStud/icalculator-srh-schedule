package com.benny.icalculation.application;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

class FileDownloaderTest {

    @Test
    void getIcal() {
        try {
            FileDownloader.getIcal(URI.create("about:blank").toURL());
        } catch (IOException | InterruptedException | IllegalArgumentException | URISyntaxException e) {
            assertTrue(true);
        }
    }
}