package com.benny.icalculation.application;

import com.benny.icalculation.application.exceptions.ConfigIncompleteException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Config {

    private static final Logger log = LogManager.getLogger(Config.class);

    private static final String userHome = System.getProperty("user.home");

    private static final String appFolderName = ".srh-schedule-ical-app";

    private static final Path appDataDirectory = Paths.get(userHome, appFolderName);

    public static Path getAppDataDirectory() {
        ensureDirectoryExists();
        return appDataDirectory;
    }

    private static void ensureDirectoryExists() {
        if (Files.notExists(appDataDirectory)) {
            try {
                Files.createDirectories(appDataDirectory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static URI getICalUri() throws ConfigIncompleteException {
        if (URI.create("").equals(iCalUri)) {
            throw new ConfigIncompleteException("No URL has been provided!");
        }
        return iCalUri;
    }

    public static void setICalUri(String iCal_url) {
        Config.iCalUri = URI.create(iCal_url);
    }

    static URI iCalUri;
    static Path configFile = Config.getAppDataDirectory().resolve("app.config");
    public static String outputFile = "out.txt";

    static boolean forceFetch = false;
    static long INVALIDATE_CACHE_AFTER_SECONDS = 3600;

    static Properties properties;

    public static boolean getForceFetch() {
        return forceFetch;
    }

    public static void setForceFetch(boolean forceFetch) {
        Config.forceFetch = forceFetch;
    }

    public static long getInvalidateCacheAfterSeconds() {
        if (forceFetch) {
            forceFetch = false;
            properties.setProperty("data.force_update", "false");
            return -5;
        }
        return INVALIDATE_CACHE_AFTER_SECONDS;
    }

    public static void loadConfig() {
        properties = new Properties();

        try (FileInputStream fis = new FileInputStream(configFile.toString())) {
            properties.load(fis);
        } catch (IOException fnfe) {
            log.error(fnfe.getMessage());
        }

        Config.setICalUri(Config.getAndPerhapsAlsoSetProperty("data.ICAL_URL", ""));

        Config.forceFetch = Config.getAndPerhapsAlsoSetProperty(
                "data.force_update", "false"
        ).equals("true");

        Runtime.getRuntime().addShutdownHook(new Thread(Config::saveConfig));
    }

    static void saveConfig() {
        log.info("Saving config");
        try (FileOutputStream fos = new FileOutputStream(configFile.toString(), false)) {
            properties.store(fos, null);
        } catch (IOException fnex) {
            System.err.println(fnex.getMessage());
        }
    }

    /**
     * Gets a property from the apps properties and, in case
     * that results in the default value (either because
     * the value hasn't been changed or the value was missing)
     * it adds the property to the new properties object.
     * <h4><b>REMEMBER TO ALSO WRITE THE PROPERTIES OBJECT TO
     * A FILE!!! IF NOT, THIS IS COMPLETELY UNNECESSARY</b></h4>
     */
    private static String getAndPerhapsAlsoSetProperty(String propertyName, String defaultValue) {
        String value = Config.properties.getProperty(propertyName, defaultValue);
        if (value.equals(defaultValue)) {
            Config.properties.setProperty(propertyName, defaultValue);
        }
        return value;
    }
}
