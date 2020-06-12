package org.fifthgen.evervet.ezyvet.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.fifthgen.evervet.ezyvet.api.callback.ConnectCallback;
import org.fifthgen.evervet.ezyvet.api.callback.GetTokenCallback;
import org.fifthgen.evervet.ezyvet.api.callback.HeadersSetUpCallback;
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

public abstract class APIBase {

    static final String API_VERSION = "/v1";
    private static final String AUTHENTICATION_PATH = "/oauth/access_token";
    Logger log;

    public APIBase() {
        log = Logger.getLogger(getClass().getName());
    }

    /**
     * Get the basic headers needed for a generic request. This method includes functionality to fetch an access token
     * if not available in the storage.
     *
     * @param tokenScope Scope of the token.
     */
    void setUpRequestHeaders(final TokenScope tokenScope, HeadersSetUpCallback callback) {
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
    void getAccessToken(TokenScope tokenScope, GetTokenCallback tokenCallback) {
        HttpPost postRequest = new HttpPost(PropertyManager.getInstance().getProperty(PropertyKey.API_URL.getKey()) + API_VERSION + AUTHENTICATION_PATH);
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

    /**
     * Generic API request which handles the setting up of the headers.
     *
     * @param request  HttpUriRequest object to use.
     * @param scope    Token scope.
     * @param callback ConnectCallback to handle the response.
     */
    void sendAPIRequest(HttpUriRequest request, TokenScope scope, ConnectCallback callback) {
        setUpRequestHeaders(scope, new HeadersSetUpCallback() {
            @Override
            public void onCompleted(Header[] headers) {
                request.setHeaders(headers);
                ConnectionManager connectionManager = ConnectionManager.getInstance();
                connectionManager.connect(request, callback);
            }

            @Override
            public void onFailed(Exception e) {
                log.severe("Failed to fetch request headers : " + e.getLocalizedMessage());
            }
        });
    }
}
