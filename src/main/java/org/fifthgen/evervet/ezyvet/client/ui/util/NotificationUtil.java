package org.fifthgen.evervet.ezyvet.client.ui.util;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.util.Duration;
import lombok.extern.java.Log;
import org.fifthgen.evervet.ezyvet.client.ui.MainController;
import org.fifthgen.evervet.ezyvet.client.ui.support.InfoNode;

import java.util.Timer;
import java.util.TimerTask;

@Log
public class NotificationUtil {

    private static final int FADEOUT_TIME = 5000;

    private NotificationUtil() {
    }

    public static void notifyError(MainController context, String message) {
        notifyMessage(context, message, "notification-error");
    }

    public static void notifyWarning(MainController context, String message) {
        notifyMessage(context, message, "notification-warning");
    }

    public static void notifyInfo(MainController context, String message) {
        notifyMessage(context, message, "notification-info");
    }

    /**
     * Show a notification message on a given label control.
     *
     * @param context    Activity exposing the label controller.
     * @param message    Message to be displayed.
     * @param styleClass Style class depicting the nature of the message.
     */
    private static void notifyMessage(MainController context, String message, String styleClass) {
        Platform.runLater(() -> {
            Label label = context.getNotificationLabel();
            label.getStyleClass().add("notification-error");
            label.setText(message);

            Timer timer = new Timer();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), label);
                        fadeOut.setFromValue(1.0);
                        fadeOut.setToValue(0.0);
                        fadeOut.play();

                        fadeOut.setOnFinished(actionEvent -> {
                            label.setText("");
                            label.getStyleClass().clear();
                        });
                    });
                }
            }, FADEOUT_TIME);
        });
    }

    public static void showAlert(Alert.AlertType type, String title, String header, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setHeaderText(title);

            InfoNode node = new InfoNode();
            node.setLabelText(header);
            node.setTextAreaText(message);
            alert.getDialogPane().setExpandableContent(node);
            alert.show();
        });
    }
}
