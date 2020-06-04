package org.fifthgen.evervet.ezyvet;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class TestContext {

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
