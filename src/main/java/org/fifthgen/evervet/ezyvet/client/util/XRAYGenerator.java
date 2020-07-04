package org.fifthgen.evervet.ezyvet.client.util;

import lombok.extern.java.Log;
import org.fifthgen.evervet.ezyvet.api.model.Animal;
import org.fifthgen.evervet.ezyvet.api.util.APIHelper;
import org.fifthgen.evervet.ezyvet.util.PropertyKey;
import org.fifthgen.evervet.ezyvet.util.PropertyManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@Log
public class XRAYGenerator {

    private static final String EXT = ".txt";

    private final String ezyVetCode;
    private final String ezyVetDesc;

    public XRAYGenerator() {
        PropertyManager propertyManager = PropertyManager.getInstance();

        this.ezyVetCode = propertyManager.getProperty(PropertyKey.IMAGING_CODE.getKey());
        this.ezyVetDesc = propertyManager.getProperty(PropertyKey.IMAGING_DESC.getKey());
    }

    public void generateXRAYFile(Animal animal) {
        APIHelper.fetchCompleteAnimalSync(animal);

        long aniID = animal.getId();
        String aniName = animal.getName();
        int ownId = animal.getContactId();
        String ownFirst = animal.getContact().getFirstName();
        String ownLast = animal.getContact().getLastName();
        String aniSp = animal.getSpecies().getName();
        String aniSex = animal.getSex().getAbbreviation();
        String aniBr = animal.getBreed().getName();
        String aniMicro = animal.getMicrochipNumber();

        String aniDOB = animal.getDateOfBirth()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
        String nowDate = LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
        String nowTime = LocalTime.now().format(DateTimeFormatter.ISO_TIME);

        Path filePath = Paths.get(PropertyManager.getInstance().getProperty(PropertyKey.X_RAY_PATH.getKey()));
        File file = filePath.resolve(ownLast + aniName + "_" + nowDate + EXT).toFile();

        try {
            if (file.createNewFile()) {
                String content = ownLast + "^" + ownFirst + "^" + ownId + "^^," +
                        aniName + "," + aniID + "," + aniSp + "," + nowDate + ", ," +
                        "InvoiceNumberHere" + "," + aniDOB + "," + aniSex + "," + ezyVetDesc + "," + ezyVetCode + "," +
                        "PVHImaging" + "," + nowTime + "," + ezyVetDesc + "," + ownFirst + " " + ownLast + "," + ownFirst + " " + ownLast + "," +
                        aniBr + "," + aniMicro + ",RFID,,OWNER,YES";

                Files.writeString(file.toPath(), content);
            }
        } catch (IOException e) {
            log.severe("Failed to create new file : " + e.getLocalizedMessage());
        }

    }
}
