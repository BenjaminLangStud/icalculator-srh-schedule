package com.benny.icalculation.application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {

    private static final Logger log = LogManager.getLogger(Config.class);
    public static String iCal_url = "";
    public static String outputFile = "out.txt";

    static boolean force_fetch = false;
    static long INVALIDATE_CACHE_AFTER_SECONDS = 3600;

    static Properties properties;

    public static long getInvalidateCacheAfterSeconds() {
        if (force_fetch) {
            force_fetch = false;
            properties.setProperty("data.force_update", "false");
            return -5;
        }
        return INVALIDATE_CACHE_AFTER_SECONDS;
    }

    static void loadConfig() {
        properties = new Properties();

        try (FileInputStream fis = new FileInputStream("app.config")) {
            properties.load(fis);
        } catch (IOException fnfe) {
            log.error(fnfe.getMessage());
        }

        Config.iCal_url = Config.getAndPerhapsAlsoSetProperty("data.ICAL_URL", "");

        Config.force_fetch = Config.getAndPerhapsAlsoSetProperty(
                "data.force_update", "false"
        ).equals("true");

        Runtime.getRuntime().addShutdownHook(new Thread(Config::saveConfig));
    }

    static void saveConfig() {
        log.info("Saving config");
        try (FileOutputStream fos = new FileOutputStream("app.config", false)) {
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
