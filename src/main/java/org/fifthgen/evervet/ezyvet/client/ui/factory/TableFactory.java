package org.fifthgen.evervet.ezyvet.client.ui.factory;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.fifthgen.evervet.ezyvet.api.model.AppointmentV2;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TableFactory {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Return a new <code>{@link TableView}</code> cell with formatted date, for the given table column.
     *
     * @param column <code>{@link TableColumn}</code> to be formatted.
     * @return The new <code>{@link TableCell}</code> with formatted date.
     */
    public static TableCell<AppointmentV2, LocalDate> dateCell(TableColumn<AppointmentV2, LocalDate> column) {
        return new TableCell<>() {

            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.format(DATE_FORMAT));
                }
            }
        };
    }
}
