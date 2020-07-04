package org.fifthgen.evervet.ezyvet.api.callback;

import org.fifthgen.evervet.ezyvet.api.model.Sex;

public interface GetSexCallback extends BasicCallback {

    void onCompleted(Sex sex);
}
