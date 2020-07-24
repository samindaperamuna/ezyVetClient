package org.fifthgen.evervet.ezyvet.api.callback;

import org.fifthgen.evervet.ezyvet.api.model.Resource;

public interface GetResourceCallback extends BasicCallback {

    void onCompleted(Resource resource);
}
