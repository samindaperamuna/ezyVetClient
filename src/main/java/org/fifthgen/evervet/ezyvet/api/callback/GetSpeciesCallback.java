package org.fifthgen.evervet.ezyvet.api.callback;

import org.fifthgen.evervet.ezyvet.api.model.Species;

public interface GetSpeciesCallback extends BasicCallback {
    void onCompleted(Species species);
}
