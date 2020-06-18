package org.fifthgen.evervet.ezyvet.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.fifthgen.evervet.ezyvet.api.callback.*;
import org.fifthgen.evervet.ezyvet.api.model.Animal;
import org.fifthgen.evervet.ezyvet.api.model.AppointmentType;
import org.fifthgen.evervet.ezyvet.api.model.Contact;
import org.fifthgen.evervet.ezyvet.api.model.TokenScope;
import org.fifthgen.evervet.ezyvet.util.PropertyKey;
import org.fifthgen.evervet.ezyvet.util.PropertyManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class APIV1 extends APIBase {

    private static final String ANIMAL_PATH = "/animal";
    private static final String CONTACT_PATH = "/contact";
    private static final String APPOINTMENT_TYPE_PATH = "/appointmenttype";

    public APIV1() {
        super.log = Logger.getLogger(getClass().getName());
    }

    public void getAnimal(int animalId, GetAnimalCallback callback) {
        final HttpGet getRequest = new HttpGet(PropertyManager.getInstance().getProperty(PropertyKey.API_URL.getKey()) + API_VERSION + ANIMAL_PATH + "?id=" + animalId);
        sendAPIRequest(getRequest, TokenScope.READ_ANIMAL, new ConnectCallback() {
            @Override
            public void onCompleted(HttpResponse response, CountDownLatch latch) {
                HttpEntity entity = response.getEntity();
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());

                try {
                    JsonNode node = mapper.readTree(EntityUtils.toString(entity));
                    JsonNode itemsNode = node.path("items");

                    List<Animal> animals = new ArrayList<>();
                    for (JsonNode itemNode : itemsNode) {
                        animals.add(mapper.readerFor(Animal.class).readValue(itemNode.get("animal")));
                    }

                    callback.onCompleted(animals.get(0));
                } catch (IOException e) {
                    String msg = "Couldn't convert response into an animal.";
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

    public void getAnimalList(GetAnimalListCallback callback) {
        final HttpGet getRequest = new HttpGet(PropertyManager.getInstance().getProperty(PropertyKey.API_URL.getKey()) + API_VERSION + ANIMAL_PATH);
        sendAPIRequest(getRequest, TokenScope.READ_ANIMAL, new ConnectCallback() {
            @Override
            public void onCompleted(HttpResponse response, CountDownLatch latch) {
                HttpEntity entity = response.getEntity();
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());

                try {
                    JsonNode node = mapper.readTree(EntityUtils.toString(entity));
                    JsonNode itemsNode = node.path("items");

                    List<Animal> animals = new ArrayList<>();
                    for (JsonNode itemNode : itemsNode) {
                        animals.add(mapper.readerFor(Animal.class).readValue(itemNode.get("animal")));
                    }

                    callback.onCompleted(animals);
                } catch (IOException e) {
                    String msg = "Couldn't convert response into a list of animals.";
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

    public void getContact(int contactId, GetContactCallback callback) {
        final HttpGet getRequest = new HttpGet(PropertyManager.getInstance().getProperty(PropertyKey.API_URL.getKey()) + API_VERSION + CONTACT_PATH + "?id=" + contactId);
        sendAPIRequest(getRequest, TokenScope.READ_CONTACT, new ConnectCallback() {
            @Override
            public void onCompleted(HttpResponse response, CountDownLatch latch) {
                HttpEntity entity = response.getEntity();
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());

                try {
                    JsonNode node = mapper.readTree(EntityUtils.toString(entity));
                    JsonNode itemsNode = node.path("items");

                    List<Contact> contacts = new ArrayList<>();
                    for (JsonNode itemNode : itemsNode) {
                        contacts.add(mapper.readerFor(Contact.class).readValue(itemNode.get("contact")));
                    }

                    callback.onCompleted(contacts.get(0));
                } catch (IOException e) {
                    String msg = "Couldn't convert response into a contact object.";
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

    public void getAppointmentTypeList(GetAppointmentTypeListCallback callback) {
        final HttpGet getRequest = new HttpGet(PropertyManager.getInstance().getProperty(PropertyKey.API_URL.getKey()) + API_VERSION + APPOINTMENT_TYPE_PATH);
        sendAPIRequest(getRequest, TokenScope.READ_APPOINTMENT_TYPE, new ConnectCallback() {
            @Override
            public void onCompleted(HttpResponse response, CountDownLatch latch) {
                HttpEntity entity = response.getEntity();
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());

                try {
                    JsonNode node = mapper.readTree(EntityUtils.toString(entity));
                    JsonNode itemsNode = node.path("items");

                    List<AppointmentType> appointmentTypes = new ArrayList<>();
                    for (JsonNode itemNode : itemsNode) {
                        appointmentTypes.add(mapper.readerFor(AppointmentType.class).readValue(itemNode.get("appointmenttype")));
                    }

                    callback.onCompleted(appointmentTypes);
                } catch (IOException e) {
                    String msg = "Couldn't convert response into a list of  appointment types.";
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
