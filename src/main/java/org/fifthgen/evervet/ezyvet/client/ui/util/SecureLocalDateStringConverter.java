package org.fifthgen.evervet.ezyvet.client.ui.util;

import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class SecureLocalDateStringConverter extends StringConverter<LocalDate> {

    /**
     * The date pattern that is used for conversion.
     */
    private static final String DATE_PATTERN = "dd/MM/yyyy";

    /**
     * The date formatter.
     */
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    private boolean hasParseError = false;

    public boolean hasParseError() {
        return hasParseError;
    }

    @Override
    public String toString(LocalDate localDate) {
        if (localDate != null)
            return DATE_FORMATTER.format(localDate);
        else return null;
    }

    @Override
    public LocalDate fromString(String formattedString) {
        try {
            LocalDate date = LocalDate.from(DATE_FORMATTER.parse(formattedString));
            hasParseError = false;
            return date;
        } catch (DateTimeParseException parseExc) {
            hasParseError = true;
            return null;
        }
    }
}