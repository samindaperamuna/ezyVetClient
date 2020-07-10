package org.fifthgen.evervet.ezyvet.client.util;

import lombok.Getter;
import lombok.extern.java.Log;
import org.fifthgen.evervet.ezyvet.api.model.Animal;
import org.fifthgen.evervet.ezyvet.api.util.APIHelper;
import org.fifthgen.evervet.ezyvet.client.ui.util.AtomicProgressCounter;
import org.fifthgen.evervet.ezyvet.util.PropertyKey;
import org.fifthgen.evervet.ezyvet.util.PropertyManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@Log
public class XRAYGenerator {

    private static final String TIME_FORMAT = "HH:mm:ss";
    private static final String EXT = ".txt";

    private final String filePattern = "%s" + File.separator + "%s_%s_%s_%s" + EXT;

    private final String ezyVetCode;
    private final String ezyVetDesc;

    @Getter
    private final AtomicProgressCounter progress;

    public XRAYGenerator() {
        PropertyManager propertyManager = PropertyManager.getInstance();
        this.progress = new AtomicProgressCounter();

        this.ezyVetCode = propertyManager.getProperty(PropertyKey.IMAGING_CODE.getKey());
        this.ezyVetDesc = propertyManager.getProperty(PropertyKey.IMAGING_DESC.getKey());
    }

    public void generateXRAYFile(final Animal animal) {
        new Thread(() -> generateXRAYProgress(animal)).start();
    }

    private void generateXRAYProgress(Animal animal) {
        APIHelper.fetchCompleteAnimalSync(animal, this.progress);

        long aniID = animal.getId();
        String aniName = animal.getName();
        int ownId = animal.getContactId();
        String ownFirst = animal.getContact().getFirstName();
        String ownLast = animal.getContact().getLastName();
        String aniSp = animal.getSpecies().getName();
        String aniSex = animal.getSex().getAbbreviation();
        String aniBr = animal.getBreed().getName();
        String aniMicro = animal.getMicrochipNumber();

        String aniDOB = "";
        Instant dobInst = animal.getDateOfBirth();

        if (dobInst != null) {
            aniDOB = dobInst.atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
        }

        String nowDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        String nowTime = LocalTime.now().format(DateTimeFormatter.ofPattern(TIME_FORMAT));

        Path dirPath = Paths.get(PropertyManager.getInstance().getProperty(PropertyKey.X_RAY_PATH.getKey()));

        // Create missing directories.
        boolean dirCreated = dirPath.toFile().mkdirs();
        if (dirCreated) {
            log.info("New directories were created.");
        }

        Path filePath = Paths.get(String.format(filePattern, dirPath, ownLast, aniName, nowDate, nowTime));
        File file = filePath.toFile();

        try {
            if (file.createNewFile()) {
                String content = ownLast + "^" + ownFirst + "^" + ownId + "^^," +
                        aniName + "," + aniID + "," + aniSp + "," + nowDate + ", ," +
                        "InvoiceNumberHere" + "," + aniDOB + "," + aniSex + "," + ezyVetDesc + "," + ezyVetCode + "," +
                        "PVHImaging" + "," + nowTime + "," + ezyVetDesc + "," + ownFirst + " " + ownLast + "," + ownFirst + " " + ownLast + "," +
                        aniBr + "," + aniMicro + ",RFID,,OWNER,YES";

                Files.writeString(file.toPath(), content);
                progressComplete();
            }
        } catch (IOException e) {
            log.severe("Failed to create new file : " + e.getLocalizedMessage());
            progress.setErrorMsg("Failed to create new file");
            progressComplete();
        }
    }

    /**
     * Advance / complete the progress counter.
     */
    private void progressComplete() {
        if (progress.get() > 80) {
            progress.set(progress.get() + 20);
        } else {
            progress.set(100);
        }
    }
}
