package org.fifthgen.evervet.ezyvet.api;

import org.fifthgen.evervet.ezyvet.TestContext;
import org.fifthgen.evervet.ezyvet.api.callback.GetAnimalListCallback;
import org.fifthgen.evervet.ezyvet.api.callback.GetAppointmentTypeListCallback;
import org.fifthgen.evervet.ezyvet.api.callback.GetContactCallback;
import org.fifthgen.evervet.ezyvet.api.callback.GetTokenCallback;
import org.fifthgen.evervet.ezyvet.api.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;

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
    public void getContactTest() {
        api.getContact(24, new GetContactCallback() {
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
