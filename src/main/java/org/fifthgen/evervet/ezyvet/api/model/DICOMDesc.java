package org.fifthgen.evervet.ezyvet.api.model;

public enum DICOMDesc {

    US_FULL_ABDOMINAL("Ultrasound Full Abdominal", Modality.US),
    US_ADRENALS_ONLY("Ultrasound Adrenals Only", Modality.US),
    US_URINARY_ONLY("Ultrasound Urinary Only", Modality.US),
    US_ECHO_LA_MEASUER("Ultrasound Echo LA Measuer", Modality.US),
    US_LIVER_ONLY("Ultrasound Liver Only", Modality.US),
    US_PREGNANCY("Ultrasound Pregnancy", Modality.US),
    US_GUIDED_SAMPLE("Ultrasound Guided Sample", Modality.US),
    US_GENERAL("Ultrasound General", Modality.US),
    US_REPEAT("Ultrasound Repeat", Modality.US),
    XRAY_ABDOMEN_2("X-Ray Abdomen 2 Views", Modality.CR),
    XRAY_ABDOMEN_3("X-Ray Abdomen 3 Views", Modality.CR),
    XRAY_CHEST_3("X-Ray Chest 3 Views", Modality.CR),
    XRAY_GENERAL_2("X-Ray General 2 Views", Modality.CR),
    XRAY_GENERAL_3("X-Ray General 3 Views", Modality.CR),
    XRAY_STIFLES_3("X-Ray Stifles 3 Views", Modality.CR),
    XRAY_HIPS_3("X-Ray Hips 3 Views", Modality.CR),
    XRAY_ELBOWS_4("X-Ray Elbows 4 Views", Modality.CR),
    XRAY_SHOULDERS_4("X-Ray Shoulders 4 Views", Modality.CR),
    XRAY_PennHIP_3("X-Ray PennHIP 3 Views", Modality.CR),
    XRAY_General("X-Ray General", Modality.CR);

    private final String name;
    private final Modality modality;

    DICOMDesc(String name, Modality modality) {
        this.name = name;
        this.modality = modality;
    }

    public String getName() {
        return name;
    }

    public Modality getModality() {
        return modality;
    }

    @Override
    public String toString() {
        return name;
    }


    public enum Modality {
        US,
        CR
    }
}
