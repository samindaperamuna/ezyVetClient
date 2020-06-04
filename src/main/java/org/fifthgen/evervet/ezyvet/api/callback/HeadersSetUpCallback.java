package org.fifthgen.evervet.ezyvet.api.callback;

import org.apache.http.Header;

public interface HeadersSetUpCallback extends BasicCallback {

    void onCompleted(Header[] headers);
}
