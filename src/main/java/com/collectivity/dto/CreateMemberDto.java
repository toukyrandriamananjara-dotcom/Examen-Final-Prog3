package com.collectivity.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Updated Request DTO for POST /members with B-2 requirements.
 * Now requires at least 2 referees with relation types.
 */
public class CreateMemberDto extends MemberInformationDto {

    private String collectivityIdentifier;
    private List<MemberRelationDto> referees = new ArrayList<>(); // Changed from List<String>
    private Boolean registrationFeePaid;
    private Boolean membershipDuesPaid;
    private Long annualContributionPaid; // New: annual contribution amount paid

    public CreateMemberDto() {}

    public String getCollectivityIdentifier() { return collectivityIdentifier; }
    public void setCollectivityIdentifier(String collectivityIdentifier) { this.collectivityIdentifier = collectivityIdentifier; }

    public List<MemberRelationDto> getReferees() { return referees; }
    public void setReferees(List<MemberRelationDto> referees) { this.referees = referees; }

    public Boolean getRegistrationFeePaid() { return registrationFeePaid; }
    public void setRegistrationFeePaid(Boolean registrationFeePaid) { this.registrationFeePaid = registrationFeePaid; }

    public Boolean getMembershipDuesPaid() { return membershipDuesPaid; }
    public void setMembershipDuesPaid(Boolean membershipDuesPaid) { this.membershipDuesPaid = membershipDuesPaid; }

    public Long getAnnualContributionPaid() { return annualContributionPaid; }
    public void setAnnualContributionPaid(Long annualContributionPaid) { this.annualContributionPaid = annualContributionPaid; }
}