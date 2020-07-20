package org.fifthgen.evervet.ezyvet.client.ui.callback;

public interface FileWriterCallback {

    void onFileWritten();

    void onFileFailed(Exception e);
}
