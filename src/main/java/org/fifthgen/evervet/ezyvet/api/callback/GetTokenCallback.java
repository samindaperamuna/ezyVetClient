package org.fifthgen.evervet.ezyvet.api.callback;

import org.fifthgen.evervet.ezyvet.api.model.Token;

public interface GetTokenCallback extends BasicCallback {

    void onCompleted(Token token);
}
