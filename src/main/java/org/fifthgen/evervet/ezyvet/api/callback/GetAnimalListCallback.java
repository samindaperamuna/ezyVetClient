package org.fifthgen.evervet.ezyvet.api.callback;

import org.fifthgen.evervet.ezyvet.api.model.Animal;

import java.util.List;

public interface GetAnimalListCallback extends BasicCallback {

    void onCompleted(List<Animal> animalList);
}
