package com.collectivity.dto;

public class CashAccountDto extends FinancialAccountDto {
    private String collectivityId;

    public CashAccountDto() {}

    public String getCollectivityId() { return collectivityId; }
    public void setCollectivityId(String collectivityId) { this.collectivityId = collectivityId; }
}