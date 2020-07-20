package org.fifthgen.evervet.ezyvet.client.util;

import lombok.extern.java.Log;
import org.fifthgen.evervet.ezyvet.util.PropertyKey;
import org.fifthgen.evervet.ezyvet.util.PropertyManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Log
public class XRAYGenerator extends FileGenerator {

    public XRAYGenerator() {
        PropertyManager propertyManager = PropertyManager.getInstance();
        ezyVetDesc = propertyManager.getProperty(PropertyKey.IMAGING_DESC.getKey());
        dirPath = Paths.get(propertyManager.getProperty(PropertyKey.X_RAY_PATH.getKey()));

        extension = "txt";
    }

    /**
     * X-RAY file logic.
     */
    protected void writeFile() {
        try {
            if (file.createNewFile()) {
                String content = ownLast + "^" + ownFirst + "^" + ownId + "^^," +
                        aniName + "," + aniID + "," + aniSp + "," + nowDate + ", ," +
                        "InvoiceNumberHere" + "," + aniDOB + "," + aniSex + "," + ezyVetDesc + "," + ezyVetCode + "," +
                        "PVHImaging" + "," + nowTime + "," + ezyVetDesc + "," + ownFirst + " " + ownLast + "," + ownFirst + " " + ownLast + "," +
                        aniBr + "," + aniMicro + ",RFID,,OWNER,YES";

                Files.writeString(file.toPath(), content);
                fileWriterCallback.onFileWritten();
                progressComplete();
            }
        } catch (IOException e) {
            log.severe("Failed to create new file : " + e.getLocalizedMessage());
            progress.setErrorMsg("Failed to create new file");
            fileWriterCallback.onFileFailed(e);
            progressComplete();
        }
    }
}
