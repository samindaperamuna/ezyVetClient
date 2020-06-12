package org.fifthgen.evervet.ezyvet.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;

@Data
public class Resource {

    private Integer id;

    private Integer active;

    @JsonProperty("created_at")
    private Instant createdAt;

    @JsonProperty("modified_at")
    private Instant modifiedAt;

    private String name;

    private String type;

    private String access;

    @JsonProperty("ownership_id")
    private Integer ownershipId;
}
