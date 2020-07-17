module evervet.ezyvet {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.desktop;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpasyncclient;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires org.kordamp.ikonli.javafx;
    requires static lombok;

    exports org.fifthgen.evervet.ezyvet.api;
    exports org.fifthgen.evervet.ezyvet.api.callback;
    exports org.fifthgen.evervet.ezyvet.api.model;

    exports org.fifthgen.evervet.ezyvet.client to javafx.graphics;

    opens org.fifthgen.evervet.ezyvet.client.ui to javafx.fxml;
    opens org.fifthgen.evervet.ezyvet.api.model to com.fasterxml.jackson.databind;
}