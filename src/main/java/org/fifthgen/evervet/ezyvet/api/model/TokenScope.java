package org.fifthgen.evervet.ezyvet.api.model;

public enum TokenScope {
    READ_ANIMAL("read-animal"),
    READ_CONTACT("read-contact");

    private final String scope;

    TokenScope(String scope) {
        this.scope = scope;
    }

    public String getScope() {
        return scope;
    }
}
