package org.fifthgen.evervet.ezyvet.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.fifthgen.evervet.ezyvet.api.callback.GetAnimalsListCallback;
import org.fifthgen.evervet.ezyvet.api.callback.GetTokenCallback;
import org.fifthgen.evervet.ezyvet.api.model.Animal;
import org.fifthgen.evervet.ezyvet.api.model.Token;
import org.fifthgen.evervet.ezyvet.api.model.TokenScope;
import org.fifthgen.evervet.ezyvet.api.model.exception.BadRequestException;
import org.fifthgen.evervet.ezyvet.api.model.exception.NotFoundException;
import org.fifthgen.evervet.ezyvet.api.model.exception.RequestCancelledException;
import org.fifthgen.evervet.ezyvet.api.model.exception.UnauthorizedException;
import org.fifthgen.evervet.ezyvet.util.ConnectionManager;
import org.fifthgen.evervet.ezyvet.util.PropertyKey;
import org.fifthgen.evervet.ezyvet.util.PropertyManager;
import org.fifthgen.evervet.ezyvet.util.TokenStorage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

public class APIV1 {

    private final Logger log;

    private static final String BASE = "/v1";
    private static final String AUTHENTICATION = "/oauth/access_token";
    private static final String ANIMAL = "/animal";

    public APIV1() {
        log = Logger.getLogger(getClass().getName());
    }

    /**
     * Get the basic headers needed for a generic request. This method includes functionality to fetch an access token
     * if not available in the storage.
     *
     * @param tokenScope Scope of the token.
     * @return Array of headers.
     */
    private Header[] setUpRequestHeaders(final TokenScope tokenScope) {
        Header[] headers = new Header[1];
        Semaphore semaphore = new Semaphore(0);

        // Check whether the token store has a given token. If not fetch a new one.
        if (!TokenStorage.getInstance().hasToken(tokenScope)) {
            getAccessToken(tokenScope, new GetTokenCallback() {
                @Override
                public void onCompleted(Token token) {
                    TokenStorage.getInstance().storeToken(tokenScope, token);
                    semaphore.release();
                }

                @Override
                public void onFailed(Exception e) {
                    log.severe(String.format("Failed to fetch token on scope %s : %s", tokenScope.getScope(), e.getLocalizedMessage()));
                    semaphore.release();
                }
            });
        }

        // Wait for the async call to complete and get the semaphore to fetch the acquired token.
        try {
            semaphore.acquire();
            Token token = TokenStorage.getInstance().getToken(tokenScope);

            if (token != null) {
                headers[0] = new BasicHeader(HttpHeaders.AUTHORIZATION, String.format("%s %s", token.getTokenType(), token.getAccessToken()));
            }
        } catch (InterruptedException e) {
            log.severe("Async method interrupted : " + e.getLocalizedMessage());
        }

        return headers;
    }

    /**
     * Acquire an access token from the API for the given scope.
     * @param tokenScope TokenScope to acquire the token for.
     * @param callback GetTokenCallback to handle the acquired token.
     */
    public void getAccessToken(TokenScope tokenScope, GetTokenCallback callback) {
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
        connectionManager.connect(postRequest, new FutureCallback<>() {
            @Override
            public void completed(HttpResponse response) {
                switch (response.getStatusLine().getStatusCode()) {
                    case 200:
                        HttpEntity entity = response.getEntity();
                        ObjectMapper mapper = new ObjectMapper();

                        try {
                            callback.onCompleted(mapper.readValue(EntityUtils.toString(entity, StandardCharsets.UTF_8), Token.class));
                            return;
                        } catch (IOException e) {
                            String msg = "Couldn't convert response into a Token object.";
                            log.severe(msg + ": \n" + e.getLocalizedMessage());
                            callback.onFailed(e);
                        }

                        break;
                    case 400:
                        log.severe("400 - Bad request");
                        callback.onFailed(new BadRequestException());

                        break;
                    case 401:
                        log.severe("401 - Unauthorized");
                        callback.onFailed(new UnauthorizedException());

                        break;
                    case 404:
                        log.severe("404 - Not found");
                        callback.onFailed(new NotFoundException());

                        break;
                }
            }

            @Override
            public void failed(Exception e) {
                log.severe("Exception in request: \n" + e.getLocalizedMessage());
                callback.onFailed(e);
            }

            @Override
            public void cancelled() {
                log.severe("Request cancelled");
                callback.onFailed(new RequestCancelledException());
            }
        });
    }

    public void getAnimalsList(GetAnimalsListCallback callback) {
        HttpGet getRequest = new HttpGet(PropertyManager.getInstance().getProperty(PropertyKey.API_URL.getKey()) + BASE + ANIMAL);
        getRequest.setHeaders(setUpRequestHeaders(TokenScope.READ_ANIMAL));

        ConnectionManager connectionManager = ConnectionManager.getInstance();
        connectionManager.connect(getRequest, new FutureCallback<>() {
            @Override
            public void completed(HttpResponse response) {
                switch (response.getStatusLine().getStatusCode()) {
                    case 200:
                        HttpEntity entity = response.getEntity();
                        ObjectMapper mapper = new ObjectMapper();

                        try {
                            CollectionType userType = mapper.getTypeFactory().constructCollectionType(List.class, Animal.class);
                            callback.onCompleted(mapper.readValue(EntityUtils.toString(entity, StandardCharsets.UTF_8), userType));
                            return;
                        } catch (IOException e) {
                            String msg = "Couldn't convert response into a list of animals.";
                            log.severe(msg + ": \n" + e.getLocalizedMessage());
                            callback.onFailed(e);
                        }

                        break;
                    case 400:
                        log.severe("400 - Bad request");
                        callback.onFailed(new BadRequestException());

                        break;
                    case 401:
                        log.severe("401 - Unauthorized");
                        callback.onFailed(new UnauthorizedException());

                        break;
                    case 404:
                        log.severe("404 - Not found");
                        callback.onFailed(new NotFoundException());

                        break;
                }
            }

            @Override
            public void failed(Exception e) {
                log.severe("Exception in request: \n" + e.getLocalizedMessage());
                callback.onFailed(e);
            }

            @Override
            public void cancelled() {
                log.severe("Request cancelled");
                callback.onFailed(new RequestCancelledException());
            }
        });
    }
}
