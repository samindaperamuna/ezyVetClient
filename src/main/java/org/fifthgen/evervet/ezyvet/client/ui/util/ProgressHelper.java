package org.fifthgen.evervet.ezyvet.client.ui.util;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lombok.extern.java.Log;
import org.fifthgen.evervet.ezyvet.client.ui.MainController;
import org.fifthgen.evervet.ezyvet.client.ui.ProgressController;

import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

@Log
public class ProgressHelper {

    private static final int TIMEOUT = 30000;
    private static final int FADEOUT_TIME = 5000;

    public static void initProgressBar(MainController context) {
        ProgressBar progressBar = context.getProgressBar();
        progressBar.setProgress(0);

        Platform.runLater(() -> {
            progressBar.setVisible(true);

            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), progressBar);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();

            fadeIn.setOnFinished(actionEvent -> toggleMenus(context, true));
        });

        // Fail-safe
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                fadeOut(context, progressBar);
            }
        }, TIMEOUT);
    }

    public static void setProgress(MainController context, PropertyChangeEvent event) {
        AtomicProgressCounter counter = (AtomicProgressCounter) event.getSource();

        ProgressBar progressBar = context.getProgressBar();
        progressBar.setProgress((int) event.getNewValue() / 100.0);

        String errMsg;
        if (!(errMsg = counter.getErrorMsg()).isEmpty()) {
            NotificationUtil.notifyError(context, errMsg);
        }

        if (progressBar.getProgress() == 1.0) {
            fadeOut(context, progressBar);
        }
    }

    public static ProgressController createProgressView(Stage stage) {
        ProgressController controller = null;
        FXMLLoader loader = new FXMLLoader(ProgressHelper.class.getResource("progress.fxml"));

        try {
            // Load and getOrders the about controller.
            Parent root = loader.load();
            controller = loader.getController();

            Stage progressStage = new Stage();
            progressStage.setScene(new Scene(root));
            progressStage.initOwner(stage.getOwner());
            progressStage.setResizable(false);
            progressStage.initModality(Modality.APPLICATION_MODAL);
            progressStage.initStyle(StageStyle.UNDECORATED);

            // Calculate the center position of the parent Stage
            double centerXPosition = stage.getX() + stage.getWidth() / 2d;
            double centerYPosition = stage.getY() + stage.getHeight() * 3 / 5d;

            // Hide the pop-up stage before it is shown and becomes relocated
            progressStage.setOnShowing(ev -> progressStage.hide());

            // Relocate the pop-up Stage
            progressStage.setOnShown(ev -> {
                progressStage.setX(centerXPosition - progressStage.getWidth() / 2d);
                progressStage.setY(centerYPosition - progressStage.getHeight() * 3 / 5d);
                progressStage.show();
            });

            controller.stage = progressStage;
        } catch (IOException e) {
            log.severe("Couldn't load FXML file: " + e.getLocalizedMessage());
        }

        return controller;
    }

    /**
     * Fadeout the progress bar and enable the X-RAY menu item.
     *
     * @param context     MainController instance.
     * @param progressBar ProgressBar instance.
     */
    private static void fadeOut(MainController context, ProgressBar progressBar) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), progressBar);
                    fadeOut.setFromValue(1.0);
                    fadeOut.setToValue(0.0);
                    fadeOut.play();

                    fadeOut.setOnFinished(actionEvent -> {
                        toggleMenus(context, false);

                        progressBar.setVisible(false);
                    });
                });
            }
        }, FADEOUT_TIME);
    }

    /**
     * Enable / disable menus and context menus that might cause issues with the async http requests.
     *
     * @param context MainController instance.
     * @param disable Whether to disable the menu.
     */
    private static void toggleMenus(MainController context, boolean disable) {
        MenuItem xrayMenu = context.getMnuItmXRay();
        MenuItem dicomMenu = context.getMnuItmDICOM();
        ContextMenu ctxMenu = context.getTableContextMenu();

        xrayMenu.setDisable(disable);
        dicomMenu.setDisable(disable);
        ctxMenu.getItems().forEach(item -> item.setDisable(disable));
    }
}
