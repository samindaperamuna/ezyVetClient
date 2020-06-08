package org.fifthgen.evervet.ezyvet.api.model;

public enum TokenScope {
    READ_ANIMAL("read-animal"),
    READ_CONTACT("read-contact"),
    READ_APPOINTMENT("read-appointment"),
    READ_APPOINTMENT_TYPE("read-appointmenttype");

    private final String scope;

    TokenScope(String scope) {
        this.scope = scope;
    }

    public String getScope() {
        return scope;
    }
}
