package org.fifthgen.evervet.ezyvet.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.fifthgen.evervet.ezyvet.api.callback.*;
import org.fifthgen.evervet.ezyvet.api.model.Animal;
import org.fifthgen.evervet.ezyvet.api.model.Contact;
import org.fifthgen.evervet.ezyvet.api.model.Token;
import org.fifthgen.evervet.ezyvet.api.model.TokenScope;
import org.fifthgen.evervet.ezyvet.util.ConnectionManager;
import org.fifthgen.evervet.ezyvet.util.PropertyKey;
import org.fifthgen.evervet.ezyvet.util.PropertyManager;
import org.fifthgen.evervet.ezyvet.util.TokenStorage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class APIV1 {

    private final Logger log;

    private static final String BASE = "/v1";
    private static final String AUTHENTICATION = "/oauth/access_token";
    private static final String ANIMAL = "/animal";
    private static final String CONTACT = "/contact";

    public APIV1() {
        log = Logger.getLogger(getClass().getName());
    }

    /**
     * Get the basic headers needed for a generic request. This method includes functionality to fetch an access token
     * if not available in the storage.
     *
     * @param tokenScope Scope of the token.
     */
    private void setUpRequestHeaders(final TokenScope tokenScope, HeadersSetUpCallback callback) {
        Header[] headers = new Header[1];

        // Check whether the token store has a given token. If not fetch a new one.
        if (!TokenStorage.getInstance().hasToken(tokenScope)) {
            getAccessToken(tokenScope, new GetTokenCallback() {
                @Override
                public void onCompleted(Token token) {
                    TokenStorage.getInstance().storeToken(tokenScope, token);
                    headers[0] = new BasicHeader(HttpHeaders.AUTHORIZATION, String.format("%s %s", token.getTokenType(), token.getAccessToken()));
                    callback.onCompleted(headers);
                }

                @Override
                public void onFailed(Exception e) {
                    callback.onFailed(e);
                }
            });
        } else {
            Token token = TokenStorage.getInstance().getToken(tokenScope);
            headers[0] = new BasicHeader(HttpHeaders.AUTHORIZATION, String.format("%s %s", token.getTokenType(), token.getAccessToken()));
            callback.onCompleted(headers);
        }
    }

    /**
     * Acquire an access token from the API for the given scope.
     *
     * @param tokenScope    TokenScope to acquire the token for.
     * @param tokenCallback GetTokenCallback to handle the acquired token.
     */
    public void getAccessToken(TokenScope tokenScope, GetTokenCallback tokenCallback) {
        HttpPost postRequest = new HttpPost(PropertyManager.getInstance().getProperty(PropertyKey.API_URL.getKey()) + BASE + AUTHENTICATION);
        postRequest.setHeader(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded"));

        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("partner_id", PropertyManager.getInstance().getProperty(PropertyKey.PARTNER_ID.getKey())));
        parameters.add(new BasicNameValuePair("client_id", PropertyManager.getInstance().getProperty(PropertyKey.CLIENT_ID.getKey())));
        parameters.add(new BasicNameValuePair("client_secret", PropertyManager.getInstance().getProperty(PropertyKey.CLIENT_SECRET.getKey())));
        parameters.add(new BasicNameValuePair("grant_type", "client_credentials"));
        parameters.add(new BasicNameValuePair("scope", tokenScope.getScope()));

        postRequest.setEntity(new UrlEncodedFormEntity(parameters, StandardCharsets.UTF_8));

        ConnectionManager connectionManager = ConnectionManager.getInstance();
        connectionManager.connect(postRequest, new ConnectCallback() {
            @Override
            public void onCompleted(HttpResponse response, CountDownLatch latch) {
                HttpEntity entity = response.getEntity();
                ObjectMapper mapper = new ObjectMapper();

                try {
                    tokenCallback.onCompleted(mapper.readValue(EntityUtils.toString(entity, StandardCharsets.UTF_8), Token.class));
                } catch (IOException e) {
                    String msg = "Couldn't convert response into a Token object.";
                    log.severe(msg + ": \n" + e.getLocalizedMessage());
                    tokenCallback.onFailed(e);
                } finally {
                    latch.countDown();
                }
            }

            @Override
            public void onFailed(Exception e) {
                log.severe("Exception in request: \n" + e.getLocalizedMessage());
                tokenCallback.onFailed(e);
            }
        });
    }

    public void getAnimalsList(GetAnimalsListCallback callback) {
        final HttpGet getRequest = new HttpGet(PropertyManager.getInstance().getProperty(PropertyKey.API_URL.getKey()) + BASE + ANIMAL);
        setUpRequestHeaders(TokenScope.READ_ANIMAL, new HeadersSetUpCallback() {
            @Override
            public void onCompleted(Header[] headers) {
                getRequest.setHeaders(headers);
                ConnectionManager connectionManager = ConnectionManager.getInstance();
                connectionManager.connect(getRequest, new ConnectCallback() {
                    @Override
                    public void onCompleted(HttpResponse response, CountDownLatch latch) {
                        HttpEntity entity = response.getEntity();
                        ObjectMapper mapper = new ObjectMapper();

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

            @Override
            public void onFailed(Exception e) {
                log.severe("Failed to fetch request headers : " + e.getLocalizedMessage());
            }
        });
    }

    public void getContact(int contactId, GetContactCallback callback) {
        final HttpGet getRequest = new HttpGet(PropertyManager.getInstance().getProperty(PropertyKey.API_URL.getKey()) + BASE + CONTACT + "?id=" + contactId);
        setUpRequestHeaders(TokenScope.READ_CONTACT, new HeadersSetUpCallback() {
            @Override
            public void onCompleted(Header[] headers) {
                getRequest.setHeaders(headers);
                ConnectionManager connectionManager = ConnectionManager.getInstance();
                connectionManager.connect(getRequest, new ConnectCallback() {
                    @Override
                    public void onCompleted(HttpResponse response, CountDownLatch latch) {
                        HttpEntity entity = response.getEntity();
                        ObjectMapper mapper = new ObjectMapper();

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
                        callback.onFailed(e);
                    }
                });
            }

            @Override
            public void onFailed(Exception e) {
                log.severe("Failed to fetch request headers : " + e.getLocalizedMessage());
            }
        });
    }
}
