package org.fifthgen.evervet.ezyvet.client.util;

import lombok.extern.java.Log;
import org.fifthgen.evervet.ezyvet.api.model.Address;
import org.fifthgen.evervet.ezyvet.api.model.Contact;
import org.fifthgen.evervet.ezyvet.api.model.DICOMDesc;
import org.fifthgen.evervet.ezyvet.api.model.DICOMDesc.Modality;
import org.fifthgen.evervet.ezyvet.api.util.APIHelper;
import org.fifthgen.evervet.ezyvet.util.PropertyKey;
import org.fifthgen.evervet.ezyvet.util.PropertyManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Log
public class DICOMGenerator extends FileGenerator {

    private static final String LINE_END = System.getProperty("line.separator");

    private final Contact ezyVetVet;
    private final Modality ezyVetModality;

    private String ownAdd;
    private String ownSub;
    private String ownPCode;

    public DICOMGenerator(DICOMDesc dicomDesc, Contact ezyVetVet) {
        PropertyManager propertyManager = PropertyManager.getInstance();
        dirPath = Paths.get(propertyManager.getProperty(PropertyKey.DICOM_PATH.getKey()));

        this.ezyVetDesc = dicomDesc.getName();
        this.ezyVetModality = dicomDesc.getModality();
        this.ezyVetVet = ezyVetVet;

        extension = "txt";
    }

    /**
     * Fetch the owner physical address details.
     *
     * @param contact Owner object.
     */
    private void fetchContactAddressProgress(Contact contact) {
        APIHelper.fetchCompleteContactSync(animal.getContact(), this.progress);

        Address ownerAddress = animal.getContact().getAddressPhysical();

        if (contact == null) {
            throw new IllegalStateException("Owner physical address is not available");
        } else {
            ownAdd = ownerAddress.getStreet1();
            ownSub = ownerAddress.getSuburb();
            ownPCode = ownerAddress.getPostCode();
        }
    }

    @Override
    protected void writeFile() {
        new Thread(() -> fetchContactAddressProgress(animal.getContact())).start();

        String diDate = nowDate;
        String studyDate = nowDate;
        String ownTitle = "";
        long ezyVetAccession = aniID;

        String ezyVetStation;
        if (ezyVetModality == Modality.US) {
            ezyVetStation = "MYLAB";
        } else {
            ezyVetStation = "METRON_Ae";
        }

        try {
            if (file.createNewFile()) {
                String content = "(0008,0005) CS [ISO_IR 100]" + LINE_END
                        + "(0008,0020) DA [" + diDate + "]" + LINE_END
                        + "(0008,0021) DA [" + diDate + "]" + LINE_END
                        + "(0008,0050) SH [" + ezyVetAccession + "]" + LINE_END
                        + "(0008,0060) CS [" + ezyVetModality + "]" + LINE_END
                        + "(0008,0080) LO [Prahran Veterinary Hospital]" + LINE_END
                        + "(0008,0090) PN [" + ezyVetVet + "]" + LINE_END
                        + "(0008,1030) LO [" + aniName + " on " + studyDate + "]" + LINE_END
                        + "(0008,103e) LO [" + ezyVetDesc + "]" + LINE_END
                        + "(0008,1050) PN [" + ezyVetVet + "]" + LINE_END
                        + "(0010,0010) PN [" + ownLast + "^" + aniName + "]" + LINE_END
                        + "(0010,0020) LO [" + aniID + "]" + LINE_END
                        + "(0010,0030) DA [" + aniDOB + "]" + LINE_END
                        + "(0010,0040) DA [" + aniSex + "]" + LINE_END
                        + "(0010,1000) LO [" + aniMicro + "]" + LINE_END
                        + "(0010,1002) SQ" + LINE_END
                        + "(fffe,e000) na" + LINE_END
                        + "(0010,0020) LO [" + aniMicro + "]" + LINE_END
                        + "(0010,0022) CS [RFID]" + LINE_END
                        + "(fffe,e00d)" + LINE_END
                        + "(fffe,e0dd)" + LINE_END
                        + "(0010,1030) DS [" + aniWt + "]" + LINE_END
                        + "(0010,1040) LO [" + ownAdd + " " + ownSub + " " + ownPCode + "]" + LINE_END
                        + "(0010,2201) LO [" + aniSp.toUpperCase() + "]" + LINE_END;

                if (aniSp.equalsIgnoreCase("Feline")) {
                    content += "(0010,2202) SQ" + LINE_END
                            + "(0010,2202) " + LINE_END
                            + "(0008,0100) SH [L-80A00]" + LINE_END
                            + "(0008,0102) SH [SRT]" + LINE_END
                            + "(0008,0104) LO [Felis Catis]" + LINE_END
                            + "(fffe,e00d)" + LINE_END
                            + "(fffe,e0dd)" + LINE_END;
                } else {
                    content += "(0010,2202) SQ" + LINE_END
                            + "(0010,2202) na" + LINE_END
                            + "(0008,0100) SH [L-80700]" + LINE_END
                            + "(0008,0102) SH [SRT]" + LINE_END
                            + "(0008,0104) LO [Canis Lupis Familiaris]" + LINE_END
                            + "(fffe,e00d)" + LINE_END
                            + "(fffe,e0dd)" + LINE_END;
                }

                content += "(0010,2203) CS [" + aniNeuter + "]" + LINE_END
                        + "(0010,2292) LO [" + aniBr + "]" + LINE_END
                        + "(0010,2297) PN [" + ownTitle + " " + ownLast + " " + aniName + "]" + LINE_END
                        + "(0010,2298) CS [OWNER]" + LINE_END
                        + "(0032,1032) PN [" + ezyVetVet + "]" + LINE_END
                        + "(0032,1060) SH [" + ezyVetDesc + "]" + LINE_END
                        + "(0040,0100) SQ" + LINE_END
                        + "(fffe,e000) " + LINE_END

                        + "(0040,0001) AE [" + ezyVetStation + "]" + LINE_END
                        + "(0040,0002) DA [" + diDate + "]" + LINE_END
                        + "(0040,0003) DA [" + nowTime + "]" + LINE_END
                        + "(0040,1001) SH [" + ezyVetCode + "]" + LINE_END
                        + "(0040,1003) SH [NORMAL]" + LINE_END
                        + "(fffe,e00d)" + LINE_END
                        + "(fffe,e0dd)" + LINE_END;

                Files.writeString(file.toPath(), content);
                progressComplete();
            }
        } catch (IOException e) {
            log.severe("Failed to create new file : " + e.getLocalizedMessage());
            progress.setErrorMsg("Failed to create new file");
            progressComplete();
        }
    }
}
