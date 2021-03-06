package org.fifthgen.evervet.ezyvet.api.model;

public enum TokenScope {
    READ_ANIMAL("read-animal"),
    READ_SPECIES("read-species"),
    READ_SEX("read-sex"),
    READ_BREED("read-breed"),
    READ_CONTACT("read-contact"),
    READ_ADDRESS("read-address"),
    READ_RESOURCE("read-resource"),
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
