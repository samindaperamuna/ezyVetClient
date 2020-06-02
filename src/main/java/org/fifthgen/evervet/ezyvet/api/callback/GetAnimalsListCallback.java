package org.fifthgen.evervet.ezyvet.api.callback;

import org.fifthgen.evervet.ezyvet.api.model.Animal;

import java.util.List;

public interface GetAnimalsListCallback extends ConnectCallback {

    void onCompleted(List<Animal> animalList);
}
