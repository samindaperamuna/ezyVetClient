package org.fifthgen.evervet.ezyvet.client.ui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import org.fifthgen.evervet.ezyvet.api.APIV1;
import org.fifthgen.evervet.ezyvet.api.callback.GetContactListCallback;
import org.fifthgen.evervet.ezyvet.api.model.Animal;
import org.fifthgen.evervet.ezyvet.api.model.Contact;
import org.fifthgen.evervet.ezyvet.api.model.DICOMDesc;
import org.fifthgen.evervet.ezyvet.client.ui.util.NotificationUtil;
import org.fifthgen.evervet.ezyvet.client.ui.util.ProgressHelper;
import org.fifthgen.evervet.ezyvet.client.util.DICOMGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Log
public class DICOMController {

    public Stage stage;

    private MainController parent;
    private Animal animal;

    @FXML
    private ChoiceBox<Contact> vetChoiceBox;

    @FXML
    private ChoiceBox<DICOMDesc> descChoiceBox;

    @FXML
    private Button generateButton;

    public void init(MainController parent, Animal animal) {
        this.parent = parent;
        this.animal = animal;

        fetchActiveVets();
        fetchStudyDescriptions();
        generateButton.setDisable(false);
    }

    @FXML
    public void onGenerateAction() {
        if (!vetChoiceBox.getSelectionModel().isEmpty() && !descChoiceBox.getSelectionModel().isEmpty()) {
            disableControls(true);

            Contact vet = vetChoiceBox.getValue();
            DICOMDesc desc = descChoiceBox.getValue();

            ProgressHelper.initProgressBar(parent);

            DICOMGenerator generator = new DICOMGenerator(desc, vet);
            generator.getProgress().setPropertyChangeListener(event -> {
                ProgressHelper.setProgress(parent, event);

                if (event.getNewValue().equals(100)) {
                    disableControls(false);
                    Platform.runLater(stage::close);
                }
            });
            generator.generateFile(this.animal);
        } else {
            log.warning("Selection empty.");
            NotificationUtil.notifyWarning(this.parent, "Please select a row first!");
        }
    }

    /**
     * Fetch the active vets from the API. Does this on a background thread to prevent locking of the UI thread.
     */
    private void fetchActiveVets() {
        ProgressController progress = ProgressHelper.createProgressView(this.parent.stage);
        progress.stage.show();

        APIV1 api = new APIV1();
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            api.getContactList(true, true, new GetContactListCallback() {
                @Override
                public void onCompleted(List<Contact> contactList) {
                    Platform.runLater(() -> {
                        vetChoiceBox.setItems(FXCollections.observableList(contactList));
                        vetChoiceBox.getSelectionModel().selectFirst();
                    });

                    Platform.runLater(() -> {
                        progress.stage.close();
                        stage.show();
                    });
                }

                @Override
                public void onFailed(Exception e) {
                    String msg = "Failed to fetch active vets";
                    log.severe(msg + " :" + e.getLocalizedMessage());

                    Platform.runLater(() -> {
                        progress.stage.close();
                        stage.close();
                    });
                }
            });
        });
    }

    private void disableControls(boolean disable) {
        this.vetChoiceBox.setDisable(disable);
        this.descChoiceBox.setDisable(disable);
        this.generateButton.setDisable(disable);
    }

    /**
     * Load the study descriptions from {@link DICOMDesc} enumeration.
     */
    private void fetchStudyDescriptions() {
        descChoiceBox.setItems(FXCollections.observableList(Arrays.asList(DICOMDesc.values())));
        descChoiceBox.getSelectionModel().selectFirst();
    }
}
