package org.fifthgen.evervet.ezyvet.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class Contact {

    private Integer id;

    private Integer active;

    @JsonProperty("created_at")
    private Date createdAt;

    @JsonProperty("modified_at")
    private Date modifiedAt;

    private String code;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("business_name")
    private String businessName;

    @JsonProperty("is_business")
    private Integer isBusiness;

    @JsonProperty("is_customer")
    private Integer isCustomer;

    @JsonProperty("is_supplier")
    private Integer isSupplier;

    @JsonProperty("is_vet")
    private Integer isVet;

    @JsonProperty("is_syndicate")
    private Integer isSyndicate;

    @JsonProperty("is_staff_member")
    private Integer isStaffMember;

    @JsonProperty("stop_credit")
    private String stopCredit;

    @JsonProperty("contact_detail_list")
    private Integer[] contactDetailsIdList;

    @JsonProperty("address_physical")
    private Integer addressPhysicalId;

    @JsonProperty("address_postal")
    private Integer addressPostalId;

    @JsonProperty("ownership_id")
    private Integer ownershipId;
}

