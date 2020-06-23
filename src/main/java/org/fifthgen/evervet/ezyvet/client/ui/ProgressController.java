package org.fifthgen.evervet.ezyvet.client.ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import lombok.extern.java.Log;

import java.net.URL;
import java.util.ResourceBundle;

@Log
public class ProgressController implements Initializable {

    public Stage stage;

    @FXML
    private ImageView imageView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Image image = new Image(getClass().getResourceAsStream("images/loading.gif"));
        imageView.setImage(image);
    }
}
