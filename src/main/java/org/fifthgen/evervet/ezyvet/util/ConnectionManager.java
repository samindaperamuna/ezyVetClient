package org.fifthgen.evervet.ezyvet.util;

import lombok.extern.java.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.ssl.SSLContextBuilder;
import org.fifthgen.evervet.ezyvet.api.callback.ConnectCallback;
import org.fifthgen.evervet.ezyvet.api.model.exception.BadRequestException;
import org.fifthgen.evervet.ezyvet.api.model.exception.NotFoundException;
import org.fifthgen.evervet.ezyvet.api.model.exception.RequestCancelledException;
import org.fifthgen.evervet.ezyvet.api.model.exception.UnauthorizedException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Log
public class ConnectionManager {

    private static final int TIMEOUT = 2000;
    private static ConnectionManager instance;


    /**
     * Private constructor ensures the instantiation of this class remains singleton.
     */
    private ConnectionManager() {
    }

    /**
     * Get the singleton instance of the class. Initiate first if null.
     *
     * @return Singleton instance of the class.
     */
    public static ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
        }

        return instance;
    }

    /**
     * Check whether the application can connect to the remote host.
     *
     * @return True if can connect, else false.
     */
    public boolean checkConnection() {
        String apiUrl = PropertyManager.getInstance().getProperty(PropertyKey.API_URL.getKey());

        try {
            URL url = new URL(apiUrl);

            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(url.getHost(), 80), TIMEOUT);

                if (socket.isConnected()) {
                    return true;
                } else {
                    log.info("Cannot connect to the remote host.");
                    return false;
                }
            }
        } catch (IOException e) {
            log.severe("An error occurred while connecting to the remote host.");
        }

        return false;
    }

    /**
     * Execute the given request in the <code>{@link HttpAsyncClient}</code>.
     * Returns a <code>{@link HttpResponse}</code> as a <code>{@link FutureCallback}</code>.
     *
     * @param request  The request object that needs executing.
     * @param callback <code>{@link ConnectCallback}</code> containing the response.
     */
    public void connect(HttpUriRequest request, ConnectCallback callback) {
        SSLContextBuilder sslContextBuilder = new SSLContextBuilder();

        try {
            sslContextBuilder.loadTrustMaterial(null, new TrustSelfSignedStrategy());

            try (CloseableHttpAsyncClient client = HttpAsyncClients.custom()
                    .setSSLContext(sslContextBuilder.build())
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .build()) {
                CountDownLatch latch = new CountDownLatch(1);
                client.start();
                client.execute(request, new FutureCallback<>() {
                    @Override
                    public void completed(HttpResponse response) {
                        switch (response.getStatusLine().getStatusCode()) {
                            case 200:
                                log.fine("200 - Success");
                                callback.onCompleted(response, latch);

                                break;
                            case 400:
                                log.severe("400 - Bad request");
                                callback.onFailed(new BadRequestException(), latch);

                                break;
                            case 401:
                                log.severe("401 - Unauthorized");
                                callback.onFailed(new UnauthorizedException(), latch);

                                break;
                            case 404:
                                log.severe("404 - Not found");
                                callback.onFailed(new NotFoundException(), latch);

                                break;
                        }
                    }

                    @Override
                    public void failed(Exception e) {
                        log.severe("Exception in request: \n" + e.getLocalizedMessage());
                        callback.onFailed(e, latch);
                    }

                    @Override
                    public void cancelled() {
                        log.severe("Request cancelled");
                        callback.onFailed(new RequestCancelledException(), latch);
                    }
                });

                latch.await(30, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            log.severe("Couldn't execute the request using the AsyncClient.");
        }
    }
}
