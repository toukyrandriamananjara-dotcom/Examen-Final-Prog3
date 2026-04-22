package com.collectivity.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "accountType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CashAccountDto.class, name = "CASH"),
        @JsonSubTypes.Type(value = MobileBankingAccountDto.class, name = "MOBILE_BANKING"),
        @JsonSubTypes.Type(value = BankAccountDto.class, name = "BANK_TRANSFER")
})
public abstract class FinancialAccountDto {
    private String id;
    private Double amount;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}
