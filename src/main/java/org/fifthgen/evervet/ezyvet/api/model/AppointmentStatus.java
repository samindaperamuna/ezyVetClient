package org.fifthgen.evervet.ezyvet.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class AppointmentStatus {

    private Integer id;

    private Integer active;

    @JsonProperty("created_at")
    private Date createdAt;

    @JsonProperty("modified_at")
    private Date modifiedAt;

    private String name;
}
