package org.fifthgen.evervet.ezyvet.api.model.exception;

public class BadRequestException extends APIException {

    public BadRequestException() {
        super("You've sent a bad request to the server. Possible causes are missing request headers.");
    }
}
