package com.collectivity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public class CreateMembershipFeeDto {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate eligibleFrom;

    private String frequency;
    private Double amount;
    private String label;

    public CreateMembershipFeeDto() {}

    public LocalDate getEligibleFrom() { return eligibleFrom; }
    public void setEligibleFrom(LocalDate eligibleFrom) { this.eligibleFrom = eligibleFrom; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
}