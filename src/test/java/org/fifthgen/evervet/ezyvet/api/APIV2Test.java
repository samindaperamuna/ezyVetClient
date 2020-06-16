package org.fifthgen.evervet.ezyvet.api;

import org.fifthgen.evervet.ezyvet.TestContext;
import org.fifthgen.evervet.ezyvet.api.callback.GetAppointmentV2Callback;
import org.fifthgen.evervet.ezyvet.api.callback.GetTokenCallback;
import org.fifthgen.evervet.ezyvet.api.model.AppointmentType;
import org.fifthgen.evervet.ezyvet.api.model.AppointmentV2;
import org.fifthgen.evervet.ezyvet.api.model.Token;
import org.fifthgen.evervet.ezyvet.api.model.TokenScope;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

public class APIV2Test {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private APIV2 api;
    private TestContext testContext;

    @Before
    public void setUp() {
        this.api = new APIV2();

        testContext = new TestContext();
        testContext.init();
    }

    @Test
    public void getAccessTokenTest() {
        api.getAccessToken(TokenScope.READ_APPOINTMENT, new GetTokenCallback() {
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
    public void getAppointmentListTest1() {
        api.getAppointmentList(new GetAppointmentV2Callback() {
            @Override
            public void onCompleted(List<AppointmentV2> appointmentList) {
                Assert.assertNotNull(appointmentList);
                testContext.log.info("Fetch appointment list complete: " + Arrays.toString(appointmentList.toArray()));
            }

            @Override
            public void onFailed(Exception e) {
                exception.expect(Exception.class);
                testContext.log.severe("Failed to fetch the appointment list: " + e.getLocalizedMessage());
            }
        });
    }

    @Test
    public void getAppointmentListTest2() throws ParseException {
        AppointmentType type = new AppointmentType();
        type.setId(1);

        api.getAppointmentList(type, new GetAppointmentV2Callback() {
            @Override
            public void onCompleted(List<AppointmentV2> appointmentList) {
                Assert.assertNotNull(appointmentList);
                testContext.log.info("Fetch appointment list complete: " + Arrays.toString(appointmentList.toArray()));
            }

            @Override
            public void onFailed(Exception e) {
                exception.expect(Exception.class);
                testContext.log.severe("Failed to fetch the appointment list: " + e.getLocalizedMessage());
            }
        });
    }

    @Test
    public void getAppointmentListTest3() throws ParseException {
        Instant instant = ZonedDateTime.parse("2017-12-14T00:00:00Z").toInstant();
        LocalDate date = instant.atZone(ZoneId.systemDefault()).toLocalDate();

        api.getAppointmentList(date, new GetAppointmentV2Callback() {
            @Override
            public void onCompleted(List<AppointmentV2> appointmentList) {
                Assert.assertNotNull(appointmentList);
                testContext.log.info("Fetch appointment list complete: " + Arrays.toString(appointmentList.toArray()));
            }

            @Override
            public void onFailed(Exception e) {
                exception.expect(Exception.class);
                testContext.log.severe("Failed to fetch the appointment list: " + e.getLocalizedMessage());
            }
        });
    }
}
