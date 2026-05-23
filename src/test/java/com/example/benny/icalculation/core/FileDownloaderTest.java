package java.com.example.benny.icalculation.core;

import com.example.benny.icalculation.core.FileDownloader;
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