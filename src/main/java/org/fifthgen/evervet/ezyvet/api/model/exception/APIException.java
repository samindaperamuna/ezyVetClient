package org.fifthgen.evervet.ezyvet.api.model.exception;

public abstract class APIException extends Exception {

    public APIException(String msg) {
        super(msg);
    }
}
