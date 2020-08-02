package org.fifthgen.evervet.ezyvet.client.ui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import org.fifthgen.evervet.ezyvet.api.model.Animal;
import org.fifthgen.evervet.ezyvet.api.model.DICOMDesc;
import org.fifthgen.evervet.ezyvet.api.model.VETDesc;
import org.fifthgen.evervet.ezyvet.client.ui.callback.FileWriterCallback;
import org.fifthgen.evervet.ezyvet.client.ui.callback.StreamReaderCallback;
import org.fifthgen.evervet.ezyvet.client.ui.util.NotificationUtil;
import org.fifthgen.evervet.ezyvet.client.ui.util.ProgressHelper;
import org.fifthgen.evervet.ezyvet.client.util.DICOMGenerator;

import java.util.Arrays;

@Log
public class DICOMController implements FileWriterCallback {

    public Stage stage;

    private MainController parent;
    private Animal animal;

    @FXML
    private ComboBox<VETDesc> vetComboBox;

    @FXML
    private ComboBox<DICOMDesc> descComboBox;

    @FXML
    private Button generateButton;

    public void init(MainController parent, Animal animal) {
        this.parent = parent;
        this.animal = animal;

        fetchActiveVets();
        fetchStudyDescriptions();
    }

    @FXML
    public void onGenerateAction() {
        if (!vetComboBox.getSelectionModel().isEmpty() && !descComboBox.getSelectionModel().isEmpty()) {
            disableControls(true);

            VETDesc vet = vetComboBox.getValue();
            DICOMDesc desc = descComboBox.getValue();

            ProgressHelper.initProgressBar(parent);

            DICOMGenerator generator = new DICOMGenerator(desc, vet);
            generator.getProgress().setPropertyChangeListener(event -> {
                ProgressHelper.setProgress(parent, event);

                if (event.getNewValue().equals(100)) {
                    disableControls(false);
                    Platform.runLater(stage::close);
                }
            });

            generator.generateFile(this.animal, new StreamReaderCallback() {

                @Override
                public void onStdError(String msg) {
                    Platform.runLater(() -> NotificationUtil.showAlert(Alert.AlertType.ERROR,
                            "An error occurred while converting the dump.",
                            "The standard error from the process :",
                            msg));
                }

                @Override
                public void onStdOut(String msg) {
                    Platform.runLater(() -> NotificationUtil.showAlert(Alert.AlertType.INFORMATION,
                            "Dump successfully converted to DICOM format.",
                            "The standard output from the process :",
                            msg));
                }

                @Override
                public void onFailed(Exception e) {
                    String msg = "Failed to fetch the process output : " + e.getLocalizedMessage();
                    log.severe(msg);
                    NotificationUtil.notifyError(parent, msg);
                }
            }, this);
        } else {
            log.warning("Selection empty.");
            NotificationUtil.notifyWarning(this.parent, "Please select a row first!");
        }
    }

    @FXML
    public void onCancelButtonAction() {
        stage.close();
    }

    @FXML
    private void onSelectionChanged() {
        if (!vetComboBox.getSelectionModel().isEmpty() && !descComboBox.getSelectionModel().isEmpty()) {
            this.generateButton.setDisable(false);
        }
    }

    private void disableControls(boolean disable) {
        this.vetComboBox.setDisable(disable);
        this.descComboBox.setDisable(disable);
        this.generateButton.setDisable(disable);
    }

    /**
     * Load the study descriptions from {@link DICOMDesc} enumeration.
     */
    private void fetchStudyDescriptions() {
        descComboBox.setItems(FXCollections.observableList(Arrays.asList(DICOMDesc.values())));
    }

    private void fetchActiveVets() {
        vetComboBox.setItems(FXCollections.observableList(Arrays.asList(VETDesc.values())));
    }

    @Override
    public void onFileWritten() {
        NotificationUtil.notifyInfo(parent, "DICOM file written successfully.");
    }

    @Override
    public void onFileFailed(Exception e) {
        String msg = "Failed to write DICOM file: " + e.getLocalizedMessage();
        log.severe(msg);
        NotificationUtil.notifyError(parent, msg);
    }
}
