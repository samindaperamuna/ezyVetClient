package org.fifthgen.evervet.ezyvet.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class AppointmentV2 {

    private Integer id;

    @JsonProperty("created_at")
    private Date createdAt;

    @JsonProperty("modified_at")
    private Date modifiedAt;

    private Boolean active;

    @JsonProperty("start_at")
    private Date startAt;

    private Integer duration;

    @JsonProperty("type_id")
    private Integer typeId;

    @JsonProperty("status_id")
    private Integer statusId;

    private String description;

    @JsonProperty("animal_id")
    private Integer animalId;

    @JsonProperty("consult_id")
    private Integer consultId;

    @JsonProperty("contact_id")
    private Integer contactId;

    @JsonProperty("sales_resource")
    private Integer salesResource;

    private Integer[] resources;
}
