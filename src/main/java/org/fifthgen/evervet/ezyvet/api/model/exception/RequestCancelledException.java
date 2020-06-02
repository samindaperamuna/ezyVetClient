package org.fifthgen.evervet.ezyvet.api.model.exception;

public class RequestCancelledException extends APIException {

    public RequestCancelledException() {
        super("HttpRequest cancelled.");
    }
}
