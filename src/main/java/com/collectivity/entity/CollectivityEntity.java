package com.collectivity.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CollectivityEntity {

    private String id;
    private String number;
    private String name;
    private String location;
    private String agriculturalSpecialty;
    private List<String> memberIds = new ArrayList<>();
    private String presidentId;
    private String deputyPresidentId;
    private String treasurerId;
    private String secretaryId;
    private LocalDate creationDate;
    private boolean federationApproval;
    private Long annualContributionAmount;

    public CollectivityEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getAgriculturalSpecialty() { return agriculturalSpecialty; }
    public void setAgriculturalSpecialty(String agriculturalSpecialty) { this.agriculturalSpecialty = agriculturalSpecialty; }

    public List<String> getMemberIds() { return memberIds; }
    public void setMemberIds(List<String> memberIds) { this.memberIds = memberIds; }

    public String getPresidentId() { return presidentId; }
    public void setPresidentId(String presidentId) { this.presidentId = presidentId; }

    public String getDeputyPresidentId() { return deputyPresidentId; }
    public void setDeputyPresidentId(String deputyPresidentId) { this.deputyPresidentId = deputyPresidentId; }

    public String getTreasurerId() { return treasurerId; }
    public void setTreasurerId(String treasurerId) { this.treasurerId = treasurerId; }

    public String getSecretaryId() { return secretaryId; }
    public void setSecretaryId(String secretaryId) { this.secretaryId = secretaryId; }

    public LocalDate getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDate creationDate) { this.creationDate = creationDate; }

    public boolean isFederationApproval() { return federationApproval; }
    public void setFederationApproval(boolean federationApproval) { this.federationApproval = federationApproval; }

    public Long getAnnualContributionAmount() { return annualContributionAmount; }
    public void setAnnualContributionAmount(Long annualContributionAmount) { this.annualContributionAmount = annualContributionAmount; }
}