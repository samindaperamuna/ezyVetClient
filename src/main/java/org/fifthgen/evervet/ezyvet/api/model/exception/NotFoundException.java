package org.fifthgen.evervet.ezyvet.api.model.exception;

public class NotFoundException extends APIException {

    public NotFoundException() {
        super("Resource not found. Possible causes are the query returned an empty data set.");
    }
}
