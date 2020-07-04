package org.fifthgen.evervet.ezyvet.api.util;

import lombok.extern.java.Log;
import org.fifthgen.evervet.ezyvet.api.APIV1;
import org.fifthgen.evervet.ezyvet.api.callback.GetBreedCallback;
import org.fifthgen.evervet.ezyvet.api.callback.GetContactCallback;
import org.fifthgen.evervet.ezyvet.api.callback.GetSexCallback;
import org.fifthgen.evervet.ezyvet.api.callback.GetSpeciesCallback;
import org.fifthgen.evervet.ezyvet.api.model.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Log
public class APIHelper {

    private static final int REQUEST_WAIT = 30000;

    /**
     * Fetch the missing animal details asynchronously. Make sure to run this in a non UI thread to stop blocking.
     *
     * @param animal Animal object.
     */
    public static void fetchCompleteAnimalSync(final Animal animal) {
        APIV1 api = new APIV1();
        CountDownLatch latch = new CountDownLatch(4);

        // Try setting the owner of the animal.
        api.getContact(animal.getContactId(), new GetContactCallback() {
            @Override
            public void onCompleted(Contact contact) {
                animal.setContact(contact);
                latch.countDown();
            }

            @Override
            public void onFailed(Exception e) {
                log.warning(String.format("Failed to fetch contact for animal : %s ID : %d", animal.getName(), animal.getId()));
                latch.countDown();
            }
        });

        // Try setting the species property for the animal.
        api.getSpecies(animal.getSpeciesId(), new GetSpeciesCallback() {
            @Override
            public void onCompleted(Species species) {
                animal.setSpecies(species);
                latch.countDown();
            }

            @Override
            public void onFailed(Exception e) {
                log.warning(String.format("Failed to fetch species for animal : %s ID : %d", animal.getName(), animal.getId()));
                latch.countDown();
            }
        });

        // Try setting the sex property for the animal.
        api.getSex(animal.getSexId(), new GetSexCallback() {
            @Override
            public void onCompleted(Sex sex) {
                animal.setSex(sex);
                latch.countDown();
            }

            @Override
            public void onFailed(Exception e) {
                log.warning(String.format("Failed to fetch sex for animal : %s ID : %d", animal.getName(), animal.getId()));
                latch.countDown();
            }
        });

        // Try setting the breed property for the animal.
        api.getBreed(animal.getBreedId(), new GetBreedCallback() {
            @Override
            public void onCompleted(Breed breed) {
                animal.setBreed(breed);
                latch.countDown();
            }

            @Override
            public void onFailed(Exception e) {
                log.warning(String.format("Failed to fetch breed for animal : %s ID : %d", animal.getName(), animal.getId()));
                latch.countDown();
            }
        });

        try {
            latch.await(REQUEST_WAIT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.severe(String.format("Couldn't fetch species for animal nam : %s, ID : %d ", animal.getName(), animal.getId()));
        }
    }
}
