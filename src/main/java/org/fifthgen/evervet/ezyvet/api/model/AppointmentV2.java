package org.fifthgen.evervet.ezyvet.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;

@Data
public class AppointmentV2 {

    private Integer id;

    @JsonProperty("created_at")
    private Instant createdAt;

    @JsonProperty("modified_at")
    private Instant modifiedAt;

    private Boolean active;

    @JsonProperty("start_at")
    private Instant startAt;

    private Integer duration;

    @JsonProperty("event_group")
    private Integer eventGroup;

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

    @JsonProperty("cancellation_reason")
    private Integer cancellationReason;

    @JsonProperty("cancellation_reason_text")
    private String cancellationReasonText;

    private Resource[] resources;
}
