package org.fifthgen.evervet.ezyvet.api.callback;

import org.fifthgen.evervet.ezyvet.api.model.Token;

public interface GetTokenCallback extends ConnectCallback {

    void onCompleted(Token token);
}
