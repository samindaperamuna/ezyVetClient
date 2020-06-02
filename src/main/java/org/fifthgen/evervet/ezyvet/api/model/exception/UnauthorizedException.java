package org.fifthgen.evervet.ezyvet.api.model.exception;

public class UnauthorizedException extends APIException {

    public UnauthorizedException() {
        super("You're unauthorized to access this resource. Possible causes are incorrect authentication headers.");
    }
}
