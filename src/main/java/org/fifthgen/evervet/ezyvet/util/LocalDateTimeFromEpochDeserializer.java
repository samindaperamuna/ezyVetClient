package org.fifthgen.evervet.ezyvet.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class LocalDateTimeFromEpochDeserializer extends StdDeserializer<LocalDateTime> {

    private static final long serialVersionUID = 1L;

    protected LocalDateTimeFromEpochDeserializer() {
        super(LocalDate.class);
    }

    @Override
    public LocalDateTime deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        return Instant.ofEpochMilli(jp.readValueAs(Long.class)).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}