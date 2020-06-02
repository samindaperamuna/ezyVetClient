package org.fifthgen.evervet.ezyvet.util;

public enum PropertyKey {
    API_URL("api-url"),
    PARTNER_ID("partner-id"),
    CLIENT_ID("client-id"),
    CLIENT_SECRET("client-secret");

    private final String key;

    PropertyKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
