package org.fifthgen.evervet.ezyvet.client.ui.util;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.util.Duration;
import org.fifthgen.evervet.ezyvet.client.ui.MainController;

import java.beans.PropertyChangeEvent;
import java.util.Timer;
import java.util.TimerTask;

public class ProgressBarHelper {

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

            fadeIn.setOnFinished(actionEvent -> {
                MenuItem xrayMenu = context.getMnuItmXRay();
                xrayMenu.setDisable(true);
            });
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
            NotificationUtil.notifyInfo(context, "X-RAY file generated successfully.");
            fadeOut(context, progressBar);
        }
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
                        MenuItem xrayMenu = context.getMnuItmXRay();
                        xrayMenu.setDisable(false);

                        progressBar.setVisible(false);
                    });
                });
            }
        }, FADEOUT_TIME);
    }
}
