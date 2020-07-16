package org.fifthgen.evervet.ezyvet.client.ui.callback;

import org.fifthgen.evervet.ezyvet.api.callback.BasicCallback;

public interface StreamReaderCallback extends BasicCallback {

    void onStdError(String msg);

    void onStdOut(String msg);
}
