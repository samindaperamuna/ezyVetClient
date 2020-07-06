package org.fifthgen.evervet.ezyvet.api.util;

import lombok.extern.java.Log;
import org.fifthgen.evervet.ezyvet.api.APIV1;
import org.fifthgen.evervet.ezyvet.api.callback.GetAnimalCallback;
import org.fifthgen.evervet.ezyvet.api.model.Animal;
import org.fifthgen.evervet.ezyvet.client.ui.util.AtomicProgressCounter;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

@Log
public class APIHelperTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testFetchCompleteAnimalSync() {
        APIV1 apiv1 = new APIV1();
        apiv1.getAnimal(1, new GetAnimalCallback() {
            @Override
            public void onCompleted(Animal animal) {
                AtomicProgressCounter counter = new AtomicProgressCounter();
                APIHelper.fetchCompleteAnimalSync(animal, counter);
                Assert.assertNotNull(animal.getContact());
                Assert.assertNotNull(animal.getSpecies());
                Assert.assertNotNull(animal.getSex());
                log.info("Fetching info complete : " + animal);
                log.info("Progress : " + counter.get());
            }

            @Override
            public void onFailed(Exception e) {
                exception.expect(Exception.class);
                log.severe("Failed to fetch animal.");
            }
        });
    }
}
