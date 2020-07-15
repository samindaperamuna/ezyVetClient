package org.fifthgen.evervet.ezyvet.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;

@Data
public class Contact {

    private Integer id;

    private Integer active;

    @JsonProperty("created_at")
    private Instant createdAt;

    @JsonProperty("modified_at")
    private Instant modifiedAt;

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

    @JsonIgnore
    private Address addressPhysical;

    @Override
    public String toString() {
        String result = "";

        String firstName = "";
        String lastName = "";
        String businessName = "";

        if (this.firstName != null) {
            firstName = this.firstName.trim();
        }

        if (this.lastName != null) {
            lastName = this.lastName.trim();
        }

        if (this.businessName != null) {
            businessName = this.businessName.trim();
        }

        if (!firstName.isEmpty()) {
            result += firstName;

            if (!lastName.isEmpty()) {
                result += " " + lastName;
            }

            if (!businessName.isEmpty()) {
                result += ", " + businessName;
            }
        } else if (!lastName.isEmpty()) {
            result += lastName;

            if (!businessName.isEmpty()) {
                result += ", " + businessName;
            }
        } else if (!businessName.isEmpty()) {
            result += businessName;
        }

        return result;
    }
}

