package org.fifthgen.evervet.ezyvet.client.util;

import lombok.Getter;
import lombok.extern.java.Log;
import org.fifthgen.evervet.ezyvet.api.model.Animal;
import org.fifthgen.evervet.ezyvet.api.util.APIHelper;
import org.fifthgen.evervet.ezyvet.client.ui.util.AtomicProgressCounter;
import org.fifthgen.evervet.ezyvet.util.PropertyKey;
import org.fifthgen.evervet.ezyvet.util.PropertyManager;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@Log
public abstract class FileGenerator {

    protected static final String TIME_FORMAT = "HH:mm:ss";
    protected static final String FILE_PATTERN = "%s" + File.separator + "%s_%s_%s_%s.%s";
    @Getter
    protected final AtomicProgressCounter progress;
    protected String extension;
    protected String ezyVetCode;
    protected String ezyVetDesc;
    protected long aniID;
    protected String aniName;
    protected int ownId;
    protected String ownFirst;
    protected String ownLast;
    protected String aniSp;
    protected String aniSex;
    protected String aniBr;
    protected String aniMicro;
    protected String aniDOB;
    protected boolean aniNeuter;
    protected double aniWt;
    protected String nowDate;
    protected String nowTime;
    protected Animal animal;
    protected Path dirPath;
    protected File file;

    protected FileGenerator() {
        PropertyManager propertyManager = PropertyManager.getInstance();
        this.progress = new AtomicProgressCounter();

        this.ezyVetCode = propertyManager.getProperty(PropertyKey.IMAGING_CODE.getKey());
    }

    public void generateFile(final Animal animal) {
        this.animal = animal;

        new Thread(() -> generateDataProgress(animal)).start();
    }

    protected void generateDataProgress(Animal animal) {
        APIHelper.fetchCompleteAnimalSync(animal, this.progress);

        aniID = animal.getId();
        aniName = animal.getName();
        ownId = animal.getContactId();
        ownFirst = animal.getContact().getFirstName();
        ownLast = animal.getContact().getLastName();
        aniSp = animal.getSpecies().getName();
        aniSex = animal.getSex().getAbbreviation();
        aniBr = animal.getBreed().getName();
        aniMicro = animal.getMicrochipNumber();
        aniNeuter = animal.getSex().isDesexed();
        aniWt = animal.getWeight();

        aniDOB = "";
        Instant dobInst = animal.getDateOfBirth();

        if (dobInst != null) {
            aniDOB = dobInst.atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
        }

        nowDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        nowTime = LocalTime.now().format(DateTimeFormatter.ofPattern(TIME_FORMAT));

        file = createFile();
        writeFile();
    }

    /**
     * Create directories if missing and generate empty file.
     *
     * @return Empty file pointer.
     */
    protected File createFile() {
        boolean dirCreated = dirPath.toFile().mkdirs();
        if (dirCreated) {
            log.info("New directories were created.");
        }

        Path filePath = Paths.get(String.format(FILE_PATTERN, dirPath, ownLast, aniName, nowDate, nowTime, extension));
        return filePath.toFile();
    }

    /**
     * Logic to write the file to disc.
     */
    protected abstract void writeFile();

    /**
     * Advance / complete the progress counter.
     */
    protected void progressComplete() {
        if (progress.get() > 80) {
            progress.set(progress.get() + 20);
        } else {
            progress.set(100);
        }
    }
}
