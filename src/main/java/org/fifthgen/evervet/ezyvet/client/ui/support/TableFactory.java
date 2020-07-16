package org.fifthgen.evervet.ezyvet.client.ui.support;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.fifthgen.evervet.ezyvet.api.model.AppointmentV2;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TableFactory {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Return a new <code>{@link TableView}</code> cell with formatted date, for the given table column.
     *
     * @param column <code>{@link TableColumn}</code> to be formatted.
     * @return The new <code>{@link TableCell}</code> with formatted date.
     */
    public static TableCell<AppointmentV2, Instant> dateCell(TableColumn<AppointmentV2, Instant> column) {
        return new TableCell<>() {

            @Override
            protected void updateItem(Instant item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.atZone(ZoneId.systemDefault()).toLocalDate().format(DATE_FORMAT));
                }
            }
        };
    }

    /**
     * Return a new <code>{@link TableView}</code> cell with formatted time, for the given table column.
     *
     * @param column <code>{@link TableColumn}</code> to be formatted.
     * @return The new <code>{@link TableCell}</code> with formatted time.
     */
    public static TableCell<AppointmentV2, Instant> timeCell(TableColumn<AppointmentV2, Instant> column) {
        return new TableCell<>() {

            @Override
            protected void updateItem(Instant item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.atZone(ZoneId.systemDefault()).toLocalTime().format(TIME_FORMAT));
                }
            }
        };
    }
}
