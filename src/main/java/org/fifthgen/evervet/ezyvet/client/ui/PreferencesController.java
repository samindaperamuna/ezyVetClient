package org.fifthgen.evervet.ezyvet.client.ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.fifthgen.evervet.ezyvet.util.PropertyKey;
import org.fifthgen.evervet.ezyvet.util.PropertyManager;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class PreferencesController implements Initializable {

    Stage stage;

    @FXML
    private TextField partnerIdText;

    @FXML
    private TextField departmentText;

    @FXML
    private TextField xRayPathText;

    @FXML
    private TextField imagingCodeText;

    @FXML
    private TextField imagingDescText;

    @FXML
    private TextField dumpPathText;

    @FXML
    private TextField wlPathText;

    @FXML
    private TextField dicomExecPath;

    @FXML
    private TextField execParamsText;

    @FXML
    private TextField clientIdText;

    @FXML
    private TextField clientSecretText;

    @FXML
    private TextField apiUrlText;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        PropertyManager propertyManager = PropertyManager.getInstance();

        // General preferences
        partnerIdText.setText(propertyManager.getProperty(PropertyKey.PARTNER_ID.getKey()));
        departmentText.setText(propertyManager.getProperty(PropertyKey.DEPARTMENT.getKey()));

        // X-RAY preferences
        xRayPathText.setText(propertyManager.getProperty(PropertyKey.X_RAY_PATH.getKey()));
        imagingCodeText.setText(propertyManager.getProperty(PropertyKey.IMAGING_CODE.getKey()));
        imagingDescText.setText(propertyManager.getProperty(PropertyKey.IMAGING_DESC.getKey()));

        // DICOM preferences
        dumpPathText.setText(propertyManager.getProperty(PropertyKey.DICOM_PATH.getKey()));
        wlPathText.setText(propertyManager.getProperty(PropertyKey.WL_PATH.getKey()));
        dicomExecPath.setText(propertyManager.getProperty(PropertyKey.DICOM_EXEC.getKey()));
        execParamsText.setText(propertyManager.getProperty(PropertyKey.DICOM_PARAMS.getKey()));

        // Account preferences
        clientIdText.setText(propertyManager.getProperty(PropertyKey.CLIENT_ID.getKey()));
        clientSecretText.setText(propertyManager.getProperty(PropertyKey.CLIENT_SECRET.getKey()));

        // API preferences
        apiUrlText.setText(propertyManager.getProperty(PropertyKey.API_URL.getKey()));
    }

    @FXML
    void onCancelAction() {
        stage.close();
    }

    @FXML
    void onApplyAction() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm save preferences.");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to save changes?");
        alert.getButtonTypes().clear();
        alert.getButtonTypes().add(ButtonType.YES);
        alert.getButtonTypes().add(ButtonType.NO);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get().equals(ButtonType.YES)) {
            PropertyManager propertyManager = PropertyManager.getInstance();

            // General preferences
            propertyManager.setProperty(PropertyKey.PARTNER_ID.getKey(), partnerIdText.getText());
            propertyManager.setProperty(PropertyKey.DEPARTMENT.getKey(), departmentText.getText());

            // X-Ray preferences
            propertyManager.setProperty(PropertyKey.X_RAY_PATH.getKey(), xRayPathText.getText());
            propertyManager.setProperty(PropertyKey.IMAGING_CODE.getKey(), imagingCodeText.getText());
            propertyManager.setProperty(PropertyKey.IMAGING_DESC.getKey(), imagingDescText.getText());

            // DICOM preferences
            propertyManager.setProperty(PropertyKey.DICOM_PATH.getKey(), dumpPathText.getText());
            propertyManager.setProperty(PropertyKey.WL_PATH.getKey(), wlPathText.getText());
            propertyManager.setProperty(PropertyKey.DICOM_EXEC.getKey(), dicomExecPath.getText());
            propertyManager.setProperty(PropertyKey.DICOM_PARAMS.getKey(), execParamsText.getText());

            // Account preferences
            propertyManager.setProperty(PropertyKey.CLIENT_ID.getKey(), clientIdText.getText());
            propertyManager.setProperty(PropertyKey.CLIENT_SECRET.getKey(), clientSecretText.getText());

            // API preferences
            propertyManager.setProperty(PropertyKey.API_URL.getKey(), apiUrlText.getText());

            // Save to file.
            propertyManager.save();

            Logger.getGlobal().info("Preferences saved to property file.");

            stage.close();
        }
    }
}
