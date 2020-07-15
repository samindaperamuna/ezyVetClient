package org.fifthgen.evervet.ezyvet.util;

public enum PropertyKey {
    DEPARTMENT("department"),
    API_URL("api-url"),
    PARTNER_ID("partner-id"),
    CLIENT_ID("client-id"),
    CLIENT_SECRET("client-secret"),
    X_RAY_PATH("x-ray-path"),
    DICOM_PATH("dicom-path"),
    WL_PATH("wl-path"),
    DICOM_EXEC("dicom-exec"),
    DICOM_PARAMS("dicom-params"),
    IMAGING_CODE("imaging-code"),
    IMAGING_DESC("imaging-desc");

    private final String key;

    PropertyKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
