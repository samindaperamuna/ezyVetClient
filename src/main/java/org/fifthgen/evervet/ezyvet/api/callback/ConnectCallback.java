package org.fifthgen.evervet.ezyvet.api.callback;

import org.apache.http.HttpResponse;

import java.util.concurrent.CountDownLatch;

public interface ConnectCallback {

    void onCompleted(HttpResponse response, CountDownLatch latch);

    default void onFailed(Exception e, CountDownLatch latch) {
        latch.countDown();
        onFailed(e);
    }

    void onFailed(Exception e);
}
