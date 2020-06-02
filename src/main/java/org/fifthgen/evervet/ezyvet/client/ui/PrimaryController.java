package org.fifthgen.evervet.ezyvet.client.ui;

import javafx.fxml.FXML;
import org.fifthgen.evervet.ezyvet.client.Main;
import org.fifthgen.evervet.ezyvet.util.PropertyKey;
import org.fifthgen.evervet.ezyvet.util.PropertyManager;

import java.io.IOException;

public class PrimaryController {

    @FXML
    private void switchToSecondary() throws IOException {
        Main.setRoot("secondary");
    }

    @FXML
    private void authenticate() {
        PropertyManager propertyManager = PropertyManager.getInstance();
        String url = propertyManager.getProperty(PropertyKey.API_URL.getKey());
    }
}
