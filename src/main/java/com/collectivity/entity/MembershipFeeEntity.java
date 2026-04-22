package com.collectivity.entity;

import java.time.LocalDate;

public class MembershipFeeEntity {
    private String id;
    private String collectivityId;
    private LocalDate eligibleFrom;
    private String frequency;
    private Double amount;
    private String label;
    private String status;

    public MembershipFeeEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCollectivityId() { return collectivityId; }
    public void setCollectivityId(String collectivityId) { this.collectivityId = collectivityId; }

    public LocalDate getEligibleFrom() { return eligibleFrom; }
    public void setEligibleFrom(LocalDate eligibleFrom) { this.eligibleFrom = eligibleFrom; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}