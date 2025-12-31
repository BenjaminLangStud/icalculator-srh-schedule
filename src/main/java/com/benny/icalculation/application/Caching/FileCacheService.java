package com.benny.icalculation.application.Caching;

import com.benny.icalculation.application.Config;
import com.benny.icalculation.application.FileDownloader;
import com.benny.icalculation.application.exceptions.ConfigIncompleteException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;


public class FileCacheService {
    private static final Logger log = LogManager.getLogger(FileCacheService.class);

    private static final String userHome = System.getProperty("user.home");

    private static final String appFolderName = ".srh-schedule-ical-app";

    private static final Path appDataDirectory = Paths.get(userHome, appFolderName);

    private static final File cacheFile = appDataDirectory.resolve("data_cache.srh-schedule").toFile();

    public static Path getAppDataDirectory() {
        return appDataDirectory;
    }

    private static void ensureDirectoryExists() throws IOException {
        if (Files.notExists(appDataDirectory)) {
            Files.createDirectories(appDataDirectory);
        }
    }

    static CachedResponse deserialize(byte[] bytes) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        try (ObjectInput input = new ObjectInputStream(byteArrayInputStream)) {
            return (CachedResponse) input.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    static byte[] serialize(final CachedResponse cachedResponse) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(cachedResponse);
            out.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets either the cached Data or fetches it
     * @return The iCal data to use
     */
    public static String getData() throws IOException, InterruptedException {
        if (cacheFile.exists()) {
            CachedResponse cachedResponse = new CachedResponse();
            boolean success = true;
            try (FileInputStream inputStream = new FileInputStream(cacheFile)) {
                byte[] bytes = inputStream.readAllBytes();
                cachedResponse = deserialize(bytes);
            } catch (FileNotFoundException fileNotFoundException) {
                log.error(fileNotFoundException.getMessage());
                success = false;
            }

            if (success && cachedResponse.timestamp > Instant.now().getEpochSecond() + Config.getInvalidateCacheAfterSeconds()) {
                return cachedResponse.content;
            }
        }

        return refreshCache();
    }

    private static String refreshCache() throws IOException, InterruptedException {
        ensureDirectoryExists();
        log.debug("Cache expired. Fetching fresh data...");

        String freshData = "";
        try {
            freshData = FileDownloader.getIcal();
        } catch (ConfigIncompleteException configIncompleteException) {
            log.warn(configIncompleteException.getMessage());
            throw new RuntimeException(configIncompleteException.getMessage());
        }

        CachedResponse newCache = new CachedResponse(freshData);

        byte[] bytes = serialize(newCache);
        try (FileOutputStream fileInputStream = new FileOutputStream(cacheFile)) {
            fileInputStream.write(bytes);
        }

        return freshData;
    }
}
