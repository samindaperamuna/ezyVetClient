package org.fifthgen.evervet.ezyvet.api.util;

import lombok.extern.java.Log;
import org.fifthgen.evervet.ezyvet.api.APIV1;
import org.fifthgen.evervet.ezyvet.api.callback.*;
import org.fifthgen.evervet.ezyvet.api.model.*;
import org.fifthgen.evervet.ezyvet.client.ui.util.AtomicProgressCounter;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Log
public class APIHelper {

    private static final int REQUEST_WAIT = 30000;

    /**
     * Fetch the missing animal details asynchronously. Make sure to run this in a non UI thread to stop blocking.
     *
     * @param animal   Animal object.
     * @param progress Progress counter.
     */
    public static void fetchCompleteAnimalSync(final Animal animal, AtomicProgressCounter progress) {
        APIV1 api = new APIV1();
        CountDownLatch latch = new CountDownLatch(4);

        // Try setting the owner of the animal.
        api.getContact(animal.getContactId(), new GetContactCallback() {
            @Override
            public void onCompleted(Contact contact) {
                animal.setContact(contact);
                progress(progress);
                latch.countDown();
            }

            @Override
            public void onFailed(Exception e) {
                log.warning(String.format("Failed to fetch contact for animal : %s ID : %d", animal.getName(), animal.getId()));
                progress(progress);
                latch.countDown();
            }
        });

        // Try setting the species property for the animal.
        api.getSpecies(animal.getSpeciesId(), new GetSpeciesCallback() {
            @Override
            public void onCompleted(Species species) {
                animal.setSpecies(species);
                progress(progress);
                latch.countDown();
            }

            @Override
            public void onFailed(Exception e) {
                log.warning(String.format("Failed to fetch species for animal : %s ID : %d", animal.getName(), animal.getId()));
                progress(progress);
                latch.countDown();
            }
        });

        // Try setting the sex property for the animal.
        api.getSex(animal.getSexId(), new GetSexCallback() {
            @Override
            public void onCompleted(Sex sex) {
                animal.setSex(sex);
                progress(progress);
                latch.countDown();
            }

            @Override
            public void onFailed(Exception e) {
                log.warning(String.format("Failed to fetch sex for animal : %s ID : %d", animal.getName(), animal.getId()));
                progress(progress);
                latch.countDown();
            }
        });

        // Try setting the breed property for the animal.
        api.getBreed(animal.getBreedId(), new GetBreedCallback() {
            @Override
            public void onCompleted(Breed breed) {
                animal.setBreed(breed);
                progress(progress);
                latch.countDown();
            }

            @Override
            public void onFailed(Exception e) {
                log.warning(String.format("Failed to fetch breed for animal : %s ID : %d", animal.getName(), animal.getId()));
                progress(progress);
                latch.countDown();
            }
        });

        try {
            latch.await(REQUEST_WAIT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.severe(String.format("Couldn't fetch species for animal nam : %s, ID : %d ", animal.getName(), animal.getId()));
        }
    }

    /**
     * Fetch the contact address asynchronously. Make sure to run this in a non UI thread to stop blocking.
     *
     * @param contact  Contact object
     * @param progress Progress counter
     */
    public static void fetchCompleteContactSync(final Contact contact, AtomicProgressCounter progress) {
        APIV1 api = new APIV1();
        CountDownLatch latch = new CountDownLatch(1);

        // Try setting the physical address of the contact.
        api.getAddress(contact.getAddressPhysicalId(), new GetAddressCallback() {
            @Override
            public void onCompleted(Address address) {
                contact.setAddressPhysical(address);
                progress(progress);
                latch.countDown();
            }

            @Override
            public void onFailed(Exception e) {
                log.warning(String.format("Failed to fetch address for contact : %s ID : %d", contact.getLastName(), contact.getId()));
                progress(progress);
                latch.countDown();
            }
        });
    }

    /**
     * Advance the progress counter per each thread.
     *
     * @param progress Progress counter in an atomic boolean.
     */
    private static void progress(AtomicProgressCounter progress) {
        progress.set(progress.get() + 20);
    }
}
