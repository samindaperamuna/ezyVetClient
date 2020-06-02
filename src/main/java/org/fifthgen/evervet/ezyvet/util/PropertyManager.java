package org.fifthgen.evervet.ezyvet.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class PropertyManager {

    private static final String PROPERTY_FILE = "application.properties";
    private static Logger logger;

    private static final Map<PropertyKey, String> DEFAULT_VALUES = Map.ofEntries(
            Map.entry(PropertyKey.API_URL, "https://api.trial.ezyvet.com/v1"),
            Map.entry(PropertyKey.PARTNER_ID, "partner12345"),
            Map.entry(PropertyKey.CLIENT_ID, "id12345"),
            Map.entry(PropertyKey.CLIENT_SECRET, "secret12345")
    );

    private static PropertyManager instance;
    private static Properties properties;

    private PropertyManager() {
        // Initialize the logger.
        logger = Logger.getLogger(getClass().getName());
        properties = new Properties();

        // Check if a property file exists. If not generate the default one.
        if (Files.exists(Paths.get(PROPERTY_FILE))) {
            readFromFile();
        } else {
            generateDefaultProperties();
        }
    }

    public static PropertyManager getInstance() {
        if (instance == null) {
            instance = new PropertyManager();
        }

        return instance;
    }

    /**
     * Generate a default property file.
     */
    private void generateDefaultProperties() {
        for (PropertyKey key : PropertyKey.values()) {
            properties.setProperty(key.getKey(), DEFAULT_VALUES.get(key));
        }

        writeToFile();
    }

    public String getProperty(String key) {
        return properties.get(key).toString();
    }

    public String setProperty(String key, String value) {
        return properties.setProperty(key, value).toString();
    }

    private synchronized void readFromFile() {
        File file = new File(PROPERTY_FILE);

        try (Reader fis = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            properties.load(fis);
        } catch (IOException e) {
            logger.severe("Failed to load property file from class path.");
        }
    }

    private synchronized void writeToFile() {
        File file = new File(PROPERTY_FILE);

        try (FileWriter fo = new FileWriter(file)) {
            properties.store(fo, "Auto generated file. Do not change.");
        } catch (IOException e) {
            logger.severe("Failed to write property file to class path.");
        }
    }
}
