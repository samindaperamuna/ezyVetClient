package org.fifthgen.evervet.ezyvet.client.ui;

import javafx.fxml.FXML;
import org.fifthgen.evervet.ezyvet.client.Main;

import java.io.IOException;

public class SecondaryController {

    @FXML
    public void switchToPrimary() throws IOException {
        Main.setRoot("primary");
    }
}
