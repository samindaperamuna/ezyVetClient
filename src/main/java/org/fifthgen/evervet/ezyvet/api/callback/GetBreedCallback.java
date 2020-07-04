package org.fifthgen.evervet.ezyvet.api.callback;

import org.fifthgen.evervet.ezyvet.api.model.Breed;

public interface GetBreedCallback extends BasicCallback {
    void onCompleted(Breed breed);
}
