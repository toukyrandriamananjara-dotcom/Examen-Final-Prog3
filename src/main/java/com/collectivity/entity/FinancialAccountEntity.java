package com.collectivity.entity;

public abstract class FinancialAccountEntity {
    private String id;
    private String collectivityId;
    private String accountType;
    private Double amount;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCollectivityId() { return collectivityId; }
    public void setCollectivityId(String collectivityId) { this.collectivityId = collectivityId; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}