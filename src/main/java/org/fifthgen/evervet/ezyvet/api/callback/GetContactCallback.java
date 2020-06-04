package org.fifthgen.evervet.ezyvet.api.callback;

import org.fifthgen.evervet.ezyvet.api.model.Contact;

public interface GetContactCallback extends BasicCallback {

    void onCompleted(Contact contact);
}
