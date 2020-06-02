package org.fifthgen.evervet.ezyvet.api.model.exception;

public class EmptyTokenException extends APIException {

    public EmptyTokenException() {
        super("Token not found in storage. Possible causes are incorrect API credentials.");
    }
}
