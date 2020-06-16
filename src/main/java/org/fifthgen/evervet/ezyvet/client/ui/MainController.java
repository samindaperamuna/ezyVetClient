package org.fifthgen.evervet.ezyvet.client.ui;

import javafx.application.Platform;
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
import org.fifthgen.evervet.ezyvet.api.callback.GetAppointmentTypeListCallback;
import org.fifthgen.evervet.ezyvet.api.callback.GetAppointmentV2Callback;
import org.fifthgen.evervet.ezyvet.api.model.AppointmentType;
import org.fifthgen.evervet.ezyvet.api.model.AppointmentV2;
import org.fifthgen.evervet.ezyvet.client.ui.factory.TableFactory;
import org.fifthgen.evervet.ezyvet.util.ConnectionManager;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class MainController implements Initializable {

    private static final Logger LOG = Logger.getLogger(MainController.class.getName());

    private static final int EXECUTOR_DELAY = 10;
    private static final int ORDER_UPDATE_DELAY = 30;

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

        fetchAppointmentTypes();
    }

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
                    errorLabel.setText(e.getLocalizedMessage());
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
        APIV2 api = new APIV2();
        api.getAppointmentList(appointmentDate, new GetAppointmentV2Callback() {

            /**
             * Return the table row handler for the <b>order summary</b> table.
             *
             * @param tableView <code>{@link TableView}</code>
             * @return <code>{@link TableRow}</code> object with implemented handles.
             */
            private TableRow<AppointmentV2> orderSummaryRow(TableView<AppointmentV2> tableView) {
                TableRow<AppointmentV2> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (!row.isEmpty())
                        if (event.getClickCount() == 2) {
                            AppointmentV2 summary = row.getItem();
                            //viewOrder(summary.getOrderId());
                        }
                });

                return row;
            }

            @Override
            public void onCompleted(List<AppointmentV2> appointmentList) {
                String labelText;
                if (!appointmentList.isEmpty()) {
                    ObservableList<AppointmentV2> data = FXCollections.observableArrayList(appointmentList);

                    // Generate the necessary columns.
                    TableColumn<AppointmentV2, Integer> orderId = new TableColumn<>("Order Id");
                    orderId.getStyleClass().add("table-cell-center");
                    orderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));

                    TableColumn<AppointmentV2, LocalDate> date = new TableColumn<>("Order Date");
                    date.getStyleClass().add("table-cell-center");
                    date.setCellValueFactory(new PropertyValueFactory<>("date"));
                    date.setCellFactory(TableFactory::dateCell);

                    TableColumn<AppointmentV2, String> purchaseOrderNumber = new TableColumn<>("PO Number");
                    purchaseOrderNumber.getStyleClass().add("table-cell-center");
                    purchaseOrderNumber.setCellValueFactory(new PropertyValueFactory<>("purchaseOrderNumber"));

                    TableColumn<AppointmentV2, String> status = new TableColumn<>("Status");
                    status.getStyleClass().add("table-cell-center");
                    status.setCellValueFactory(new PropertyValueFactory<>("status"));

                    TableColumn<AppointmentV2, LocalDate> updatedOn = new TableColumn<>("Updated On");
                    updatedOn.getStyleClass().add("table-cell-center");
                    updatedOn.setCellValueFactory(new PropertyValueFactory<>("updatedOn"));
                    updatedOn.setCellFactory(TableFactory::dateCell);

                    // Update table data on UI thread.
                    Platform.runLater(() -> {
                        appointmentsTable.getColumns().clear();
                        appointmentsTable.getColumns().add(orderId);
                        appointmentsTable.getColumns().add(date);
                        appointmentsTable.getColumns().add(purchaseOrderNumber);
                        appointmentsTable.getColumns().add(status);
                        appointmentsTable.getColumns().add(updatedOn);
                        appointmentsTable.setRowFactory(this::orderSummaryRow);

                        appointmentsTable.setItems(data);
                    });

                    labelText = "Appointments loaded successfully.";
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
