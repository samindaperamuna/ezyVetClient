package org.fifthgen.evervet.ezyvet.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.fifthgen.evervet.ezyvet.api.callback.ConnectCallback;
import org.fifthgen.evervet.ezyvet.api.callback.GetAppointmentV2Callback;
import org.fifthgen.evervet.ezyvet.api.model.AppointmentType;
import org.fifthgen.evervet.ezyvet.api.model.AppointmentV2;
import org.fifthgen.evervet.ezyvet.api.model.TokenScope;
import org.fifthgen.evervet.ezyvet.util.PropertyKey;
import org.fifthgen.evervet.ezyvet.util.PropertyManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class APIV2 extends APIBase {

    private static final String API_REVISION = "/v2";
    private static final String APPOINTMENT_PATH = "/appointment";

    public APIV2() {
        super.log = Logger.getLogger(getClass().getName());
    }

    public void getAppointmentList(GetAppointmentV2Callback callback) {
        final HttpGet getRequest = new HttpGet(PropertyManager.getInstance().getProperty(PropertyKey.API_URL.getKey()) + API_REVISION + APPOINTMENT_PATH);
        getAppointmentList(getRequest, callback);
    }

    public void getAppointmentList(Date date, AppointmentType type, GetAppointmentV2Callback callback) {
        final HttpGet getRequest = new HttpGet(PropertyManager.getInstance().getProperty(PropertyKey.API_URL.getKey()) + API_REVISION + APPOINTMENT_PATH + "?start_at=" + date.getTime() + "&type_id=" + type.getId());
        getAppointmentList(getRequest, callback);
    }

    private void getAppointmentList(HttpGet request, GetAppointmentV2Callback callback) {
        sendAPIRequest(request, TokenScope.READ_APPOINTMENT, new ConnectCallback() {
            @Override
            public void onCompleted(HttpResponse response, CountDownLatch latch) {
                HttpEntity entity = response.getEntity();
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());

                try {
                    JsonNode node = mapper.readTree(EntityUtils.toString(entity));
                    JsonNode itemsNode = node.path("items");

                    List<AppointmentV2> appointmentTypes = new ArrayList<>();
                    for (JsonNode itemNode : itemsNode) {
                        appointmentTypes.add(mapper.readerFor(AppointmentV2.class).readValue(itemNode.get("appointment")));
                    }

                    callback.onCompleted(appointmentTypes);
                } catch (IOException e) {
                    String msg = "Couldn't convert response into a list of appointments.";
                    log.severe(msg + ": \n" + e.getLocalizedMessage());
                    callback.onFailed(e);
                } finally {
                    latch.countDown();
                }
            }

            @Override
            public void onFailed(Exception e) {
                log.severe("Exception in request: \n" + e.getLocalizedMessage());
                callback.onFailed(e);
            }
        });
    }
}
