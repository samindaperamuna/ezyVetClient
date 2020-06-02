package org.fifthgen.evervet.ezyvet.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.ssl.SSLContextBuilder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.logging.Logger;

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
                    Logger.getGlobal().info("Cannot connect to the remote host.");
                    return false;
                }
            }
        } catch (IOException e) {
            Logger.getGlobal().severe("An error occurred while connecting to the remote host.");
        }

        return false;
    }

    /**
     * Execute the given request in the <code>{@link HttpAsyncClient}</code>.
     * Returns a <code>{@link HttpResponse}</code> as a <code>{@link FutureCallback}</code>.
     *
     * @param request  The request object that needs executing.
     * @param callback <code>{@link FutureCallback}</code> containing the response.
     */
    public void connect(HttpUriRequest request, FutureCallback<HttpResponse> callback) {
        SSLContextBuilder sslContextBuilder = new SSLContextBuilder();

        try {
            sslContextBuilder.loadTrustMaterial(null, new TrustSelfSignedStrategy());

            try (CloseableHttpAsyncClient client = HttpAsyncClients.custom()
                    .setSSLContext(sslContextBuilder.build())
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .build()) {
                client.start();
                client.execute(request, callback).get();
            }
        } catch (Exception e) {
            Logger.getGlobal().severe("Couldn't execute the request using the AsyncClient.");
        }
    }
}
