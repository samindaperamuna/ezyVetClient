package org.fifthgen.evervet.ezyvet.api.callback;

import org.fifthgen.evervet.ezyvet.api.model.Animal;

public interface GetAnimalCallback extends BasicCallback {

    void onCompleted(Animal animal);
}
