package org.fifthgen.evervet.ezyvet.client.ui;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.extern.java.Log;
import org.fifthgen.evervet.ezyvet.api.APIV1;
import org.fifthgen.evervet.ezyvet.api.APIV2;
import org.fifthgen.evervet.ezyvet.api.callback.GetAnimalCallback;
import org.fifthgen.evervet.ezyvet.api.callback.GetAppointmentTypeListCallback;
import org.fifthgen.evervet.ezyvet.api.callback.GetAppointmentV2Callback;
import org.fifthgen.evervet.ezyvet.api.callback.GetResourceCallback;
import org.fifthgen.evervet.ezyvet.api.model.Animal;
import org.fifthgen.evervet.ezyvet.api.model.AppointmentType;
import org.fifthgen.evervet.ezyvet.api.model.AppointmentV2;
import org.fifthgen.evervet.ezyvet.api.model.Resource;
import org.fifthgen.evervet.ezyvet.client.ui.callback.FileWriterCallback;
import org.fifthgen.evervet.ezyvet.client.ui.support.TableFactory;
import org.fifthgen.evervet.ezyvet.client.ui.util.NotificationUtil;
import org.fifthgen.evervet.ezyvet.client.ui.util.ProgressHelper;
import org.fifthgen.evervet.ezyvet.client.util.XRAYGenerator;
import org.fifthgen.evervet.ezyvet.util.ConnectionManager;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.*;

@Log
public class MainController implements Initializable, FileWriterCallback {

    private static final int EXECUTOR_DELAY = 10;
    private static final int REQUEST_UPDATE_DELAY = 30;

    public Stage stage;

    @FXML
    @Getter
    private MenuItem mnuItmXRay;

    @FXML
    @Getter
    private MenuItem mnuItmDICOM;

    @FXML
    private DatePicker appointmentDatePicker;

    @FXML
    private ComboBox<AppointmentType> appointmentTypeCombo;

    @FXML
    private TableView<AppointmentV2> appointmentsTable;

    @FXML
    @Getter
    private ProgressBar progressBar;

    @FXML
    @Getter
    private Label notificationLabel;

    @FXML
    private Label netStatLabel;

    @Getter
    private ContextMenu tableContextMenu;

    @FXML
    private Button searchButton;

    @FXML
    private Button previousNavButton;

    @FXML
    private Button nextNavButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(2);
        exec.scheduleAtFixedRate(new NetStatUpdater(), 0, EXECUTOR_DELAY, TimeUnit.SECONDS);

        addManualAppointmentType();
        addContextMenu();

        Platform.runLater(searchButton::requestFocus);
    }

    /**
     * Create and add context menu to the appointments table.
     */
    private void addContextMenu() {
        tableContextMenu = new ContextMenu();

        MenuItem xrayCtxMnuItem = new MenuItem("Generate X-RAY Input");
        xrayCtxMnuItem.setGraphic(new FontIcon("mdi-radioactive"));
        xrayCtxMnuItem.setOnAction(actionEvent -> this.onXRAYAction());

        MenuItem dicomCtxMnuItem = new MenuItem("Generate DICOM Worklist");
        dicomCtxMnuItem.setGraphic(new FontIcon("mdi-microscope"));
        dicomCtxMnuItem.setOnAction(actionEvent -> this.onDICOMAction());

        tableContextMenu.getItems().add(xrayCtxMnuItem);
        tableContextMenu.getItems().add(dicomCtxMnuItem);
    }

    /**
     * Add the appointment type manually using hardcoded values.
     */
    private void addManualAppointmentType() {
        AppointmentType type = new AppointmentType();
        type.setId(7);
        type.setName("Surgery");
        appointmentTypeCombo.getItems().add(type);
        appointmentTypeCombo.getSelectionModel().selectFirst();
        appointmentTypeCombo.setDisable(true);
    }

    /**
     * Fetch the appointment types from the API. Does this on a background thread to prevent locking of the UI thread.
     */
    private void fetchAppointmentTypes() {
        NotificationUtil.notifyInfo(this, "Fetching appointment types. Please wait!");

        new Thread(() -> {
            APIV1 api = new APIV1();
            api.getAppointmentTypeList(new GetAppointmentTypeListCallback() {
                @Override
                public void onCompleted(List<AppointmentType> appointmentTypeList) {
                    Platform.runLater(() -> {
                        appointmentTypeCombo.setItems(FXCollections.observableList(appointmentTypeList));
                        appointmentTypeCombo.getSelectionModel().selectFirst();
                    });
                }

                @Override
                public void onFailed(Exception e) {
                    String msg = "Failed to fetch appointment types";
                    log.severe(msg + " :" + e.getLocalizedMessage());
                    NotificationUtil.notifyError(MainController.this, msg);
                }
            });
        }).start();
    }

    @FXML
    private void onPreferencesAction() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("preferences.fxml"));

        try {
            // Load preference controller.
            Parent root = loader.load();
            PreferencesController controller = loader.getController();

            Stage prefStage = new Stage();

            // Set stage.
            controller.stage = prefStage;

            prefStage.setScene(new Scene(root));
            prefStage.initOwner(stage.getOwner());
            prefStage.setResizable(false);
            prefStage.initModality(Modality.APPLICATION_MODAL);
            prefStage.initStyle(StageStyle.UNIFIED);
            prefStage.show();
        } catch (IOException e) {
            log.severe("Couldn't load FXML file: " + e.getLocalizedMessage());
        }
    }

    @FXML
    private void onXRAYAction() {
        if (!appointmentsTable.getSelectionModel().isEmpty()) {
            AppointmentV2 appointment = appointmentsTable.getSelectionModel().getSelectedItem();

            ProgressHelper.initProgressBar(this);

            XRAYGenerator generator = new XRAYGenerator();
            generator.getProgress().setPropertyChangeListener(event -> ProgressHelper.setProgress(this, event));
            generator.generateFile(appointment.getAnimal(), null, this);
        } else {
            log.warning("Selection empty.");
            NotificationUtil.notifyWarning(this, "Please select a row first!");
        }
    }

    @FXML
    private void onDICOMAction() {
        if (!appointmentsTable.getSelectionModel().isEmpty()) {
            AppointmentV2 appointment = appointmentsTable.getSelectionModel().getSelectedItem();
            FXMLLoader loader = new FXMLLoader(MainController.this.getClass().getResource("dicom.fxml"));

            try {
                // Load DICOM controller.
                Parent root = loader.load();
                DICOMController controller = loader.getController();

                Stage dicomStage = new Stage();

                // Set dicomStage.
                controller.stage = dicomStage;
                controller.init(this, appointment.getAnimal());

                dicomStage.setScene(new Scene(root));
                dicomStage.initOwner(stage.getOwner());
                dicomStage.setResizable(false);
                dicomStage.initModality(Modality.APPLICATION_MODAL);
                dicomStage.initStyle(StageStyle.UNIFIED);
                dicomStage.show();
            } catch (IOException e) {
                log.severe("Couldn't load FXML file: " + e.getLocalizedMessage());
            }
        }
    }

    @FXML
    private void onRefreshAction() {
        this.onSearchAction();
    }

    @FXML
    private void onExitAction() {
        System.exit(0);
    }

    @FXML
    private void onAboutAction() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("about.fxml"));

        try {
            // Load the about controller.
            Parent root = loader.load();

            Stage aboutStage = new Stage();
            aboutStage.setScene(new Scene(root));
            aboutStage.initOwner(stage.getOwner());
            aboutStage.setResizable(false);
            aboutStage.initModality(Modality.APPLICATION_MODAL);
            aboutStage.initStyle(StageStyle.UNIFIED);
            aboutStage.show();
        } catch (IOException e) {
            log.severe("Couldn't load FXML file: " + e.getLocalizedMessage());
        }
    }

    @FXML
    private void onNavigationButtonPress(Event event) {
        cleanDatePicker();
        LocalDate date = appointmentDatePicker.getValue();

        if (event.getTarget().equals(previousNavButton)) {
            appointmentDatePicker.setValue(date.plusDays(-1));
        } else if (event.getTarget().equals(nextNavButton)) {
            appointmentDatePicker.setValue(date.plusDays(1));
        }

        onSearchAction();
    }

    @FXML
    private void onSearchAction() {
        cleanDatePicker();
        searchAppointments(appointmentDatePicker.getValue());
    }

    /**
     * Requesting date picker value when it's null can cause issues. Hence the value is initialized to today's date.
     */
    private void cleanDatePicker() {
        if (appointmentDatePicker.getValue() == null) {
            appointmentDatePicker.setValue(LocalDate.now());
        }
    }

    private void searchAppointments(LocalDate appointmentDate) {
        NotificationUtil.notifyInfo(this, "This might take a few minutes. Please be patient!");
        toggleDisableMenuItems(true);

        AppointmentType type = appointmentTypeCombo.getSelectionModel().getSelectedItem();
        ProgressController controller = ProgressHelper.createProgressView(this.stage);
        controller.stage.show();

        // Handles the progress window.
        CountDownLatch progressLatch = new CountDownLatch(1);

        APIV2 apiv2 = new APIV2();
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            apiv2.getAppointmentList(type, appointmentDate, new GetAppointmentV2Callback() {

                /**
                 * Return the table row handler for the <b>order summary</b> table.
                 *
                 * @param tableView <code>{@link TableView}</code>
                 * @return <code>{@link TableRow}</code> object with implemented handles.
                 */
                private TableRow<AppointmentV2> tableRow(TableView<AppointmentV2> tableView) {
                    TableRow<AppointmentV2> row = new TableRow<>();
                    row.setOnMouseClicked(event -> {
                        AppointmentV2 appointment = row.getItem();

                        if (!row.isEmpty() && appointment.getAnimal() != null) {
                            toggleDisableMenuItems(false);

                            if (event.getButton() == MouseButton.PRIMARY) {
                                if (event.getClickCount() == 2) {
                                    // viewOrder(summary.getOrderId());
                                }
                            } else if (event.getButton() == MouseButton.SECONDARY) {
                                tableContextMenu.show(row, event.getScreenX(), event.getScreenY());
                            }
                        }
                    });

                    return row;
                }

                @Override
                public void onCompleted(List<AppointmentV2> appointmentList) {
                    appointmentList.removeIf(a -> a.getAnimalId() == null);
                    List<AppointmentV2> surgeryAppointments = new ArrayList<>();
                    Platform.runLater(appointmentsTable.getColumns()::clear);

                    if (!appointmentList.isEmpty()) {
                        APIV1 apiv1 = new APIV1();

                        CountDownLatch resourceLatch = new CountDownLatch(appointmentList.size());

                        for (AppointmentV2 appointmentV2 : appointmentList) {
                            Resource[] resources = appointmentV2.getResources();
                            if (resources.length > 0) {
                                Resource res = resources[0];
                                apiv1.getResource(res.getId(), new GetResourceCallback() {
                                    @Override
                                    public void onCompleted(Resource resource) {
                                        if (resource.getOwnershipId() == APIV1.PRAHRAN_SURGERY_OWNER_ID) {
                                            surgeryAppointments.add(appointmentV2);
                                        }

                                        resourceLatch.countDown();
                                    }

                                    @Override
                                    public void onFailed(Exception e) {
                                        log.warning("Failed to fetch resource info for resource: " + res.getId());
                                        resourceLatch.countDown();
                                    }
                                });
                            }
                        }

                        try {
                            resourceLatch.await(REQUEST_UPDATE_DELAY, TimeUnit.SECONDS);
                        } catch (InterruptedException e) {
                            log.warning("Failed to release countdown latch: " + e.getLocalizedMessage());
                        }

                        CountDownLatch animalCollectionLatch = new CountDownLatch(appointmentList.size());

                        for (AppointmentV2 appointmentV2 : surgeryAppointments) {
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

                                    log.severe(msg + appointmentTime + ", \n\r" + e.getLocalizedMessage());
                                    NotificationUtil.notifyError(MainController.this, msg + appointmentTime);
                                }
                            });
                        }

                        try {
                            animalCollectionLatch.await(REQUEST_UPDATE_DELAY, TimeUnit.SECONDS);

                            ObservableList<AppointmentV2> data = FXCollections.observableArrayList(surgeryAppointments);
                            FXCollections.sort(data, Comparator.comparing(AppointmentV2::getStartAt));

                            // Generate the necessary columns.
                            TableColumn<AppointmentV2, Instant> startTime = new TableColumn<>("Start Time");
                            startTime.setSortType(TableColumn.SortType.DESCENDING);
                            startTime.getStyleClass().add("table-cell-center");
                            startTime.setCellValueFactory(new PropertyValueFactory<>("startAt"));
                            startTime.setMinWidth(30.0);
                            startTime.setCellFactory(TableFactory::timeCell);

                            TableColumn<AppointmentV2, String> description = new TableColumn<>("Description");
                            description.getStyleClass().add("table-cell-left");
                            description.setCellValueFactory(new PropertyValueFactory<>("description"));

                            TableColumn<AppointmentV2, String> animalCode = new TableColumn<>("Animal Code");
                            animalCode.getStyleClass().add("table-cell-center");
                            animalCode.setCellValueFactory(cellData -> {
                                Animal animal = cellData.getValue().getAnimal();
                                return new SimpleStringProperty(animal == null ? "" : animal.getCode());
                            });

                            TableColumn<AppointmentV2, String> animalName = new TableColumn<>("Animal Name");
                            animalName.getStyleClass().add("table-cell-center");
                            animalName.setCellValueFactory(cellData -> {
                                Animal animal = cellData.getValue().getAnimal();
                                return new SimpleStringProperty(animal == null ? "" : animal.getName());
                            });

                            TableColumn<AppointmentV2, String> microchipNumber = new TableColumn<>("Microchip Number");
                            microchipNumber.getStyleClass().add("table-cell-center");
                            microchipNumber.setCellValueFactory(cellData -> {
                                Animal animal = cellData.getValue().getAnimal();
                                return new SimpleStringProperty(animal == null ? "" : animal.getMicrochipNumber());
                            });

                            // Update table data on UI thread.
                            Platform.runLater(() -> {
                                appointmentsTable.getColumns().add(startTime);
                                appointmentsTable.getColumns().add(description);
                                appointmentsTable.getColumns().add(animalCode);
                                appointmentsTable.getColumns().add(animalName);
                                appointmentsTable.getColumns().add(microchipNumber);

                                appointmentsTable.setRowFactory(this::tableRow);

                                appointmentsTable.setItems(data);
                            });

                            NotificationUtil.notifyInfo(MainController.this, "Appointments loaded successfully");
                        } catch (InterruptedException e) {
                            log.severe("Failed to release count down latch: " + e.getLocalizedMessage());
                        }
                    } else {
                        NotificationUtil.notifyInfo(MainController.this, "No appointments were found");
                    }

                    // Count down progress latch.
                    progressLatch.countDown();
                }

                @Override
                public void onFailed(Exception e) {
                    String msg = "Failed to load appointments";
                    log.severe(msg + " :" + e.getLocalizedMessage());
                    NotificationUtil.notifyError(MainController.this, msg);

                    // Count down progress latch.
                    progressLatch.countDown();
                }
            });

            /*
             * Wait for the progress latch to countdown or timeout and close the progress window.
             */
            try {
                progressLatch.await(REQUEST_UPDATE_DELAY, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.warning("Failed to close the progress latch.");
            } finally {
                Platform.runLater(controller.stage::close);
            }
        });
    }

    private void toggleDisableMenuItems(boolean disable) {
        if (mnuItmXRay.isDisable() != disable) {
            mnuItmXRay.setDisable(disable);
        }

        if (mnuItmDICOM.isDisable() != disable) {
            mnuItmDICOM.setDisable(disable);
        }
    }

    @Override
    public void onFileWritten() {
        NotificationUtil.notifyInfo(this, "X-RAY file written successfully.");
    }

    @Override
    public void onFileFailed(Exception e) {
        String msg = "Failed to write X-RAY file: " + e.getLocalizedMessage();
        log.severe(msg);
        NotificationUtil.notifyError(this, msg);
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
