package org.fifthgen.evervet.ezyvet.api.callback;

import org.fifthgen.evervet.ezyvet.api.model.Address;

public interface GetAddressCallback extends BasicCallback {

    void onCompleted(Address address);
}
