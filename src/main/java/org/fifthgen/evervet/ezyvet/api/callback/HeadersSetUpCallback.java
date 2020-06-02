package org.fifthgen.evervet.ezyvet.api.callback;

import org.apache.http.Header;

public interface HeadersSetUpCallback extends ConnectCallback {

    void onCompleted(Header[] headers);
}
