package com.collectivity.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "accountType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CashAccountWithBalanceDto.class, name = "CASH"),
        @JsonSubTypes.Type(value = MobileBankingAccountWithBalanceDto.class, name = "MOBILE_BANKING"),
        @JsonSubTypes.Type(value = BankAccountWithBalanceDto.class, name = "BANK_TRANSFER")
})
public abstract class FinancialAccountWithBalanceDto {
    private String id;
    private Double balance;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }
}