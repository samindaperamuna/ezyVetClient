package org.fifthgen.evervet.ezyvet.api.callback;

import org.fifthgen.evervet.ezyvet.api.model.Contact;

import java.util.List;

public interface GetContactListCallback extends BasicCallback {

    void onCompleted(List<Contact> contactList);
}
