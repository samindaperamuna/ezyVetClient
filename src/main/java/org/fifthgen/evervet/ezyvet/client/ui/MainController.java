package org.fifthgen.evervet.ezyvet.client.ui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.fifthgen.evervet.ezyvet.api.APIV1;
import org.fifthgen.evervet.ezyvet.api.callback.GetAppointmentTypeListCallback;
import org.fifthgen.evervet.ezyvet.api.model.AppointmentType;
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
            Logger.getGlobal().severe("Couldn't load FXML file: " + e.getLocalizedMessage());
        }
    }

    @FXML
    private void onSearchAction() {
        LocalDate appointmentDate = appointmentDatePicker.getValue();

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