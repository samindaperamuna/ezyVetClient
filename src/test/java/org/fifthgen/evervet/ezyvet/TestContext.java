package org.fifthgen.evervet.ezyvet;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class TestContext {

    public static final int ANIMAL_ID = 1;
    public static final int SPECIES_ID = 1;
    public static final int SEX_ID = 1;
    public static final int BREED_ID = 628;
    public static final int CONTACT_ID = 24;
    public static final int ADDRESS_ID = 1;
    public static final int RESOURCE_ID = 296;

    public Logger log;

    public TestContext() {
        log = Logger.getLogger(getClass().getName());
    }

    public void init() {
        try {
            LogManager.getLogManager().readConfiguration(new FileInputStream("logging.properties"));
        } catch (IOException e) {
            log.info("Couldn't read HTTP Client Logger.");
        }
    }
}
