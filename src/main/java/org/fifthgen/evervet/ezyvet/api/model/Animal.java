package org.fifthgen.evervet.ezyvet.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class Animal {

    private Long id;

    private Boolean active;

    @JsonProperty("created_at")
    private Date createdAt;

    @JsonProperty("modified_at")
    private Date modifiedAt;

    private String name;

    private String code;

    @JsonProperty("sex_id")
    private Integer sexId;

    @JsonProperty("is_dead")
    private Boolean isDead;

    @JsonProperty("is_hostile")
    private Boolean isHostile;

    @JsonProperty("animalcolour_id")
    private Integer animalColourId;

    @JsonProperty("species_id")
    private Integer speciesId;

    @JsonProperty("breed_id")
    private Integer breedId;

    @JsonProperty("rabies_number")
    private String rabiesNumber;

    @JsonProperty("date_of_rabies_vaccination")
    private Date dateOfRabiesVaccination;

    @JsonProperty("microchip_number")
    private String microchipNumber;

    @JsonProperty("contact_id")
    private Integer contactId;

    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

    @JsonProperty("is_estimated_date_of_birth")
    private Boolean isEstimatedDateOfBirth;

    @JsonProperty("date_of_death")
    private Date dateOfDeath;

    @JsonProperty("death_reason")
    private String deathReason;

    @JsonProperty("date_of_desex")
    private Date dateOfDeSex;

    @JsonProperty("referring_clinic_id")
    private Integer referringClinicId;

    @JsonProperty("referring_vet_id")
    private Integer referringVetId;

    @JsonProperty("residence_contact_id")
    private Integer residenceContactId;

    private Double weight;

    @JsonProperty("weight_unit")
    private String weightUnit;

    private Boolean resuscitate;

    private String notes;

    @JsonProperty("notes_important")
    private Boolean notesImportant;

    private String guid;
}
