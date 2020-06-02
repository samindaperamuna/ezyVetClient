module ezyVet.Client.Evervet {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpasyncclient;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires static lombok;

    exports org.fifthgen.evervet.ezyvet.client;

    opens org.fifthgen.evervet.ezyvet.client to javafx.fxml;
}