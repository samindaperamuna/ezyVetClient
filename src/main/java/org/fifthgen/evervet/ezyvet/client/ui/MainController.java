package org.fifthgen.evervet.ezyvet.client.ui;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.fifthgen.evervet.ezyvet.api.APIV1;
import org.fifthgen.evervet.ezyvet.api.APIV2;
import org.fifthgen.evervet.ezyvet.api.callback.GetAnimalCallback;
import org.fifthgen.evervet.ezyvet.api.callback.GetAppointmentTypeListCallback;
import org.fifthgen.evervet.ezyvet.api.callback.GetAppointmentV2Callback;
import org.fifthgen.evervet.ezyvet.api.model.Animal;
import org.fifthgen.evervet.ezyvet.api.model.AppointmentType;
import org.fifthgen.evervet.ezyvet.api.model.AppointmentV2;
import org.fifthgen.evervet.ezyvet.client.ui.factory.TableFactory;
import org.fifthgen.evervet.ezyvet.util.ConnectionManager;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class MainController implements Initializable {

    private static final Logger LOG = Logger.getLogger(MainController.class.getName());

    private static final int EXECUTOR_DELAY = 10;
    private static final int REQUEST_UPDATE_DELAY = 30;

    public Stage stage;

    @FXML
    private DatePicker appointmentDatePicker;

    @FXML
    private ComboBox<AppointmentType> appointmentTypeCombo;

    @FXML
    private Button searchButton;

    @FXML
    private TableView<AppointmentV2> appointmentsTable;

    @FXML
    private Label errorLabel;

    @FXML
    private Label netStatLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(2);
        exec.scheduleAtFixedRate(new NetStatUpdater(), 0, EXECUTOR_DELAY, TimeUnit.SECONDS);

        addManualAppointmentType();
    }

    /**
     * Add the appointment type manually using hardcoded values.
     */
    private void addManualAppointmentType() {
        AppointmentType type = new AppointmentType();
        type.setName("Surgery");
        appointmentTypeCombo.getItems().add(type);
        appointmentTypeCombo.getSelectionModel().selectFirst();
        appointmentTypeCombo.setDisable(true);
    }

    /**
     * Fetch the appointment types from the API. Does this on a background thread to prevent locking of the UI thread.
     */
    private void fetchAppointmentTypes() {
        errorLabel.setText("Fetching appointment types. Please wait!");

        new Thread(() -> {
            APIV1 api = new APIV1();
            api.getAppointmentTypeList(new GetAppointmentTypeListCallback() {
                @Override
                public void onCompleted(List<AppointmentType> appointmentTypeList) {
                    Platform.runLater(() -> {
                        appointmentTypeCombo.setItems(FXCollections.observableList(appointmentTypeList));
                        appointmentTypeCombo.getSelectionModel().selectFirst();
                        errorLabel.setText(null);
                    });
                }

                @Override
                public void onFailed(Exception e) {
                    Platform.runLater(() -> errorLabel.setText(e.getLocalizedMessage()));
                }
            });
        }).start();
    }

    @FXML
    private void onExitAction() {
        System.exit(0);
    }

    @FXML
    private void onAboutAction() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("about.fxml"));

        try {
            // Load and getOrders the about controller.
            Parent root = loader.load();

            Stage aboutStage = new Stage();
            aboutStage.setScene(new Scene(root));
            aboutStage.initOwner(stage.getOwner());
            aboutStage.setResizable(false);
            aboutStage.initModality(Modality.APPLICATION_MODAL);
            aboutStage.initStyle(StageStyle.UNIFIED);
            aboutStage.show();
        } catch (IOException e) {
            LOG.severe("Couldn't load FXML file: " + e.getLocalizedMessage());
        }
    }

    @FXML
    private void onSearchAction() {
        LocalDate appointmentDate = appointmentDatePicker.getValue();
        APIV2 apiv2 = new APIV2();
        apiv2.getAppointmentList(appointmentDate, new GetAppointmentV2Callback() {

            /**
             * Return the table row handler for the <b>order summary</b> table.
             *
             * @param tableView <code>{@link TableView}</code>
             * @return <code>{@link TableRow}</code> object with implemented handles.
             */
            private TableRow<AppointmentV2> tableRow(TableView<AppointmentV2> tableView) {
                TableRow<AppointmentV2> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (!row.isEmpty())
                        if (event.getClickCount() == 2) {
                            AppointmentV2 appointment = row.getItem();
                            //viewOrder(summary.getOrderId());
                        }
                });

                return row;
            }

            @Override
            public void onCompleted(List<AppointmentV2> appointmentList) {
                String labelText;
                if (!appointmentList.isEmpty()) {
                    APIV1 apiv1 = new APIV1();
                    CountDownLatch animalCollectionLatch = new CountDownLatch(appointmentList.size());

                    appointmentList.forEach(appointmentV2 -> {
                        apiv1.getAnimal(appointmentV2.getAnimalId(), new GetAnimalCallback() {
                            @Override
                            public void onCompleted(Animal animal) {
                                animalCollectionLatch.countDown();
                                if (animal != null) {
                                    appointmentV2.setAnimal(animal);
                                }
                            }

                            @Override
                            public void onFailed(Exception e) {
                                animalCollectionLatch.countDown();
                                String msg = "Failed to load animal for appointment at: ";
                                LocalTime appointmentTime = appointmentV2.getStartAt().atZone(ZoneId.systemDefault()).toLocalTime();

                                LOG.severe(msg + appointmentTime + ", \n\r" + e.getLocalizedMessage());
                                Platform.runLater(() -> errorLabel.setText(msg + appointmentTime));
                            }
                        });
                    });

                    try {
                        animalCollectionLatch.await(REQUEST_UPDATE_DELAY, TimeUnit.SECONDS);

                        ObservableList<AppointmentV2> data = FXCollections.observableArrayList(appointmentList);

                        // Generate the necessary columns.
                        TableColumn<AppointmentV2, Instant> startTime = new TableColumn<>("Start Time");
                        startTime.getStyleClass().add("table-cell-center");
                        startTime.setCellValueFactory(new PropertyValueFactory<>("startAt"));
                        startTime.setCellFactory(TableFactory::timeCell);

                        TableColumn<AppointmentV2, String> description = new TableColumn<>("Description");
                        description.getStyleClass().add("table-cell-center");
                        description.setCellValueFactory(new PropertyValueFactory<>("description"));

                        TableColumn<AppointmentV2, String> animalName = new TableColumn<>("Animal");
                        animalName.getStyleClass().add("table-cell-center");
                        animalName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAnimal().getName()));

                        // Update table data on UI thread.
                        Platform.runLater(() -> {
                            appointmentsTable.getColumns().clear();
                            appointmentsTable.getColumns().add(startTime);
                            appointmentsTable.getColumns().add(description);
                            appointmentsTable.getColumns().add(animalName);
                            appointmentsTable.setRowFactory(this::tableRow);

                            appointmentsTable.setItems(data);
                        });

                        labelText = "Appointments loaded successfully.";
                    } catch (InterruptedException e) {
                        LOG.severe("Failed to release count down latch: " + e.getLocalizedMessage());
                        return;
                    }
                } else {
                    labelText = "No appointments were found.";
                }

                Platform.runLater(() -> errorLabel.setText(labelText));
            }

            @Override
            public void onFailed(Exception e) {
                LOG.severe("Failed to load appointments: " + e.getLocalizedMessage());
                Platform.runLater(() -> errorLabel.setText("Failed to load appointments."));
            }
        });
    }

    /**
     * This class detects the current status of internet connection.
     */
    private class NetStatUpdater implements Runnable {

        private static final String OFFLINE = "Offline";
        private static final String OFFLINE_STYLE = "offline";
        private static final String ONLINE = "Online";
        private static final String ONLINE_STYLE = "online";

        @Override
        public void run() {
            ConnectionManager connectionManager = ConnectionManager.getInstance();
            if (connectionManager.checkConnection()) {
                Platform.runLater(() -> {
                    netStatLabel.setText(ONLINE);
                    netStatLabel.getStyleClass().clear();
                    netStatLabel.getStyleClass().add(ONLINE_STYLE);
                });
            } else {
                Platform.runLater(() -> {
                    netStatLabel.setText(OFFLINE);
                    netStatLabel.getStyleClass().clear();
                    netStatLabel.getStyleClass().add(OFFLINE_STYLE);
                });
            }
        }
    }
}
