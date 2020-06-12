package org.fifthgen.evervet.ezyvet.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;

@Data
public class AppointmentType {

    private Integer id;

    private Integer active;

    @JsonProperty("created_at")
    private Instant createdAt;

    @JsonProperty("modified_at")
    private Instant modifiedAt;

    private String name;

    @JsonProperty("default_duration")
    private Integer defaultDuration;

    @Override
    public String toString() {
        return name;
    }
}
