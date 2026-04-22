package com.collectivity.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Request DTO for POST /members — mirrors the OpenAPI CreateMember schema.
 * Extends MemberInformationDto via composition to reflect the allOf relationship.
 */
public class CreateMemberDto extends MemberInformationDto {

    private String collectivityIdentifier;
    private List<String> referees = new ArrayList<>();
    private Boolean registrationFeePaid;
    private Boolean membershipDuesPaid;

    public CreateMemberDto() {}

    public String getCollectivityIdentifier() { return collectivityIdentifier; }
    public void setCollectivityIdentifier(String collectivityIdentifier) { this.collectivityIdentifier = collectivityIdentifier; }

    public List<String> getReferees() { return referees; }
    public void setReferees(List<String> referees) { this.referees = referees; }

    public Boolean getRegistrationFeePaid() { return registrationFeePaid; }
    public void setRegistrationFeePaid(Boolean registrationFeePaid) { this.registrationFeePaid = registrationFeePaid; }

    public Boolean getMembershipDuesPaid() { return membershipDuesPaid; }
    public void setMembershipDuesPaid(Boolean membershipDuesPaid) { this.membershipDuesPaid = membershipDuesPaid; }
}