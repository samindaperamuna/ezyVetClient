package org.fifthgen.evervet.ezyvet.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class AppointmentType {

    private Integer id;

    private Integer active;

    @JsonProperty("created_at")
    private Date createdAt;

    @JsonProperty("modified_at")
    private Date modifiedAt;

    private String name;

    @JsonProperty("default_duration")
    private Integer defaultDuration;

    @Override
    public String toString() {
        return name;
    }
}
