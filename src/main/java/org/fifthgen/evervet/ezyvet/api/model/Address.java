package org.fifthgen.evervet.ezyvet.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;

@Data
public class Address {

    private Integer id;

    private Integer active;

    @JsonProperty("created_at")
    private Instant createdAt;

    @JsonProperty("modified_at")
    private Instant modifiedAt;

    private String name;

    @JsonProperty("street_1")
    private String street1;

    @JsonProperty("street_2")
    private String street2;

    private String suburb;

    private String city;

    private String region;

    @JsonProperty("post_code")
    private String postCode;

    @JsonProperty("country_id")
    private Integer countryId;

    private String state;

    private Double longitude;

    private Double latitude;

    @JsonIgnore
    private Object for_resource;
}
