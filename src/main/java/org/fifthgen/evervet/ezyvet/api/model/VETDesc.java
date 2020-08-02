package org.fifthgen.evervet.ezyvet.api.model;

public enum VETDesc {

    PETER_JULIFF("Peter Juliff"),
    DIANA_BARKER("Diana Barker"),
    KAY_WALLACE("Kay Wallace"),
    KIM_CASTRO9("Kim Castro"),
    SANJANA_ASWANI("Sanjana Aswani"),
    SARAH_BUSH("Sarah Bush"),
    SARAH_CARTER("Sarah Carter"),
    YUBIN_KANG("Yubin Kang"),
    ANDRES_TOWNSEND("Andres Townsend");

    private final String name;

    VETDesc(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
