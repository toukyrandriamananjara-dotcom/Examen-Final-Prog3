package com.collectivity.dto;

public class CreateMemberPaymentDto {
    private Double amount;
    private String membershipFeeIdentifier;
    private String accountCreditedIdentifier;
    private String paymentMode;

    public CreateMemberPaymentDto() {}

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getMembershipFeeIdentifier() { return membershipFeeIdentifier; }
    public void setMembershipFeeIdentifier(String membershipFeeIdentifier) { this.membershipFeeIdentifier = membershipFeeIdentifier; }

    public String getAccountCreditedIdentifier() { return accountCreditedIdentifier; }
    public void setAccountCreditedIdentifier(String accountCreditedIdentifier) { this.accountCreditedIdentifier = accountCreditedIdentifier; }

    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }
}