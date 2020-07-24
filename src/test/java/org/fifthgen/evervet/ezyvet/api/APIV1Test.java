package org.fifthgen.evervet.ezyvet.api;

import org.fifthgen.evervet.ezyvet.TestContext;
import org.fifthgen.evervet.ezyvet.api.callback.*;
import org.fifthgen.evervet.ezyvet.api.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;

import static org.fifthgen.evervet.ezyvet.TestContext.*;

public class APIV1Test {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private APIV1 api;
    private TestContext testContext;

    @Before
    public void setUp() {
        this.api = new APIV1();

        testContext = new TestContext();
        testContext.init();
    }

    @Test
    public void getAccessTokenTest() {
        api.getAccessToken(TokenScope.READ_ANIMAL, new GetTokenCallback() {
            @Override
            public void onCompleted(Token token) {
                Assert.assertNotNull(token);
                testContext.log.info("Fetch token complete: " + token.toString());
            }

            @Override
            public void onFailed(Exception e) {
                exception.expect(Exception.class);
                testContext.log.severe("Failed to fetch the token: " + e.getLocalizedMessage());
            }
        });
    }

    @Test
    public void getAnimalTest() {
        api.getAnimal(ANIMAL_ID, new GetAnimalCallback() {
            @Override
            public void onCompleted(Animal animal) {
                Assert.assertNotNull(animal);
                testContext.log.info("Fetch animal complete : " + animal);
            }

            @Override
            public void onFailed(Exception e) {
                exception.expect(Exception.class);
                testContext.log.severe("Failed to fetch animal: " + e.getLocalizedMessage());
            }
        });
    }

    @Test
    public void getAnimalsListTest() {
        api.getAnimalList(new GetAnimalListCallback() {
            @Override
            public void onCompleted(List<Animal> animalList) {
                Assert.assertNotNull(animalList);
                testContext.log.info("Fetch animals list complete : " + Arrays.toString(animalList.toArray()));
            }

            @Override
            public void onFailed(Exception e) {
                exception.expect(Exception.class);
                testContext.log.severe("Failed to fetch animals list: " + e.getLocalizedMessage());
            }
        });
    }

    @Test
    public void getSpeciesTest() {
        api.getSpecies(SPECIES_ID, new GetSpeciesCallback() {
            @Override
            public void onCompleted(Species species) {
                Assert.assertNotNull(species);
                testContext.log.info("Fetch species complete : " + species);
            }

            @Override
            public void onFailed(Exception e) {
                exception.expect(Exception.class);
                testContext.log.severe("Failed to fetch species: " + e.getLocalizedMessage());
            }
        });
    }

    @Test
    public void getSexTest() {
        api.getSex(SEX_ID, new GetSexCallback() {
            @Override
            public void onCompleted(Sex sex) {
                Assert.assertNotNull(sex);
                testContext.log.info("Fetch sex complete : " + sex);
            }

            @Override
            public void onFailed(Exception e) {
                exception.expect(Exception.class);
                testContext.log.severe("Failed to fetch sex: " + e.getLocalizedMessage());
            }
        });
    }

    @Test
    public void getBreedTest() {
        api.getBreed(BREED_ID, new GetBreedCallback() {
            @Override
            public void onCompleted(Breed breed) {
                Assert.assertNotNull(breed);
                testContext.log.info("Fetch breed complete : " + breed);
            }

            @Override
            public void onFailed(Exception e) {
                exception.expect(Exception.class);
                testContext.log.severe("Failed to fetch breed: " + e.getLocalizedMessage());
            }
        });
    }

    @Test
    public void getContactListTest() {
        api.getContactList(true, true, new GetContactListCallback() {
            @Override
            public void onCompleted(List<Contact> contactList) {
                Assert.assertNotNull(contactList);
                testContext.log.info("Fetch contact list complete : " + Arrays.toString(contactList.toArray()));
            }

            @Override
            public void onFailed(Exception e) {
                exception.expect(Exception.class);
                testContext.log.severe("Failed to fetch contact list: " + e.getLocalizedMessage());
            }
        });
    }

    @Test
    public void getContactTest() {
        api.getContact(CONTACT_ID, new GetContactCallback() {
            @Override
            public void onCompleted(Contact contact) {
                Assert.assertNotNull(contact);
                testContext.log.info("Fetch contact complete : " + contact.toString());
            }

            @Override
            public void onFailed(Exception e) {
                exception.expect(Exception.class);
                testContext.log.severe("Failed to fetch contact: " + e.getLocalizedMessage());
            }
        });
    }

    @Test
    public void getAddressTest() {
        api.getAddress(ADDRESS_ID, new GetAddressCallback() {
            @Override
            public void onCompleted(Address address) {
                Assert.assertNotNull(address);
                testContext.log.info("Fetch address complete : " + address.toString());
            }

            @Override
            public void onFailed(Exception e) {
                exception.expect(Exception.class);
                testContext.log.severe("Failed to fetch address: " + e.getLocalizedMessage());
            }
        });
    }

    @Test
    public void getResourceTest() {
        api.getResource(RESOURCE_ID, new GetResourceCallback() {
            @Override
            public void onCompleted(Resource resource) {
                Assert.assertNotNull(resource);
                testContext.log.info("Fetch resource complete : " + resource.toString());
            }

            @Override
            public void onFailed(Exception e) {
                exception.expect(Exception.class);
                testContext.log.severe("Failed to fetch resource: " + e.getLocalizedMessage());
            }
        });
    }

    @Test
    public void getAppointmentTypeListTest() {
        api.getAppointmentTypeList(new GetAppointmentTypeListCallback() {
            @Override
            public void onCompleted(List<AppointmentType> appointmentTypes) {
                Assert.assertNotNull(appointmentTypes);
                testContext.log.info("Fetch appointment type list complete : " + Arrays.toString(appointmentTypes.toArray()));
            }

            @Override
            public void onFailed(Exception e) {
                exception.expect(Exception.class);
                testContext.log.severe("Failed to fetch appointment type list : " + e.getLocalizedMessage());
            }
        });
    }
}
