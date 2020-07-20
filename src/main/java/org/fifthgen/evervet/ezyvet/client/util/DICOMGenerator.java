package org.fifthgen.evervet.ezyvet.client.util;

import lombok.extern.java.Log;
import org.fifthgen.evervet.ezyvet.api.model.Address;
import org.fifthgen.evervet.ezyvet.api.model.Contact;
import org.fifthgen.evervet.ezyvet.api.model.DICOMDesc;
import org.fifthgen.evervet.ezyvet.api.model.DICOMDesc.Modality;
import org.fifthgen.evervet.ezyvet.api.util.APIHelper;
import org.fifthgen.evervet.ezyvet.client.ui.callback.StreamReaderCallback;
import org.fifthgen.evervet.ezyvet.util.PropertyKey;
import org.fifthgen.evervet.ezyvet.util.PropertyManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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

        extension = "dump";
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
                        + "(0010,0040) CS [" + aniSex + "]" + LINE_END
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
                            + "(fffe,e000) " + LINE_END
                            + "(0008,0100) SH [L-80A00]" + LINE_END
                            + "(0008,0102) SH [SRT]" + LINE_END
                            + "(0008,0104) LO [Felis Catis]" + LINE_END
                            + "(fffe,e00d)" + LINE_END
                            + "(fffe,e0dd)" + LINE_END;
                } else {
                    content += "(0010,2202) SQ" + LINE_END
                            + "(fffe,e000) " + LINE_END
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
                        + "(0032,1060) LO [" + ezyVetDesc + "]" + LINE_END
                        + "(0040,0100) SQ" + LINE_END
                        + "(fffe,e000) " + LINE_END

                        + "(0040,0001) AE [" + ezyVetStation + "]" + LINE_END
                        + "(0040,0002) DA [" + diDate + "]" + LINE_END
                        + "(0040,0003) TM [" + nowTime + "]" + LINE_END
                        + "(0040,1001) SH [" + ezyVetCode + "]" + LINE_END
                        + "(0040,1003) SH [NORMAL]" + LINE_END
                        + "(fffe,e00d)" + LINE_END
                        + "(fffe,e0dd)" + LINE_END;

                Files.writeString(file.toPath(), content);
                convertToWorklist();
                fileWriterCallback.onFileWritten();
                progressComplete();
            }
        } catch (IOException | InterruptedException e) {
            String msg = "Failed to create new file : " + e.getLocalizedMessage();
            log.severe(msg);
            progress.setErrorMsg(msg);
            fileWriterCallback.onFileWritten();
            progressComplete();
        }
    }

    private void convertToWorklist() throws IOException, InterruptedException {
        PropertyManager properties = PropertyManager.getInstance();

        String exec = properties.getProperty(PropertyKey.DICOM_EXEC.getKey());
        String params = properties.getProperty(PropertyKey.DICOM_PARAMS.getKey());
        String dicomPath = file.getPath();
        Path wlPath = Paths.get(properties.getProperty(PropertyKey.WL_PATH.getKey()));

        boolean dirCreated = wlPath.toFile().mkdirs();
        if (dirCreated) {
            log.info("New directories were created.");
        }

        String wlFile = wlPath + File.separator + file.getName().replace(extension, "wl");

        List<String> commands = new ArrayList<>();
        commands.add(exec);
        commands.addAll(Arrays.asList(params.split(" ")));
        commands.add(dicomPath);
        commands.add(wlFile);

        Process process = new ProcessBuilder(commands).start();

        readInputStream(process.getErrorStream(), streamReaderCallback, true);
        readInputStream(process.getInputStream(), streamReaderCallback, false);
        process.waitFor();
    }

    private void readInputStream(InputStream stream, StreamReaderCallback callback, boolean isStdErr) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try (BufferedReader err = new BufferedReader(new InputStreamReader(stream))) {
                final StringBuilder buffer = new StringBuilder();
                String line;

                while ((line = err.readLine()) != null) {
                    buffer.append(line).append(LINE_END);
                }

                String msg = buffer.toString();

                if (isStdErr && !msg.isEmpty()) {
                    callback.onStdError(msg);
                } else if (!msg.isEmpty()) {
                    callback.onStdOut(msg);
                }
            } catch (IOException e) {
                log.warning("Failed to read input stream content : " + e.getLocalizedMessage());
                callback.onFailed(e);
            }
        });
    }
}
