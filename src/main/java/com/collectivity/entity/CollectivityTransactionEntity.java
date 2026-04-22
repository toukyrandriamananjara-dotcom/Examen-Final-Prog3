package com.collectivity.entity;

import java.time.LocalDate;

public class CollectivityTransactionEntity {
    private String id;
    private String collectivityId;
    private String memberId;
    private String membershipFeeId;
    private LocalDate creationDate;
    private Double amount;
    private String paymentMode;
    private String accountCreditedId;

    public CollectivityTransactionEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCollectivityId() { return collectivityId; }
    public void setCollectivityId(String collectivityId) { this.collectivityId = collectivityId; }

    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }

    public String getMembershipFeeId() { return membershipFeeId; }
    public void setMembershipFeeId(String membershipFeeId) { this.membershipFeeId = membershipFeeId; }

    public LocalDate getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDate creationDate) { this.creationDate = creationDate; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }

    public String getAccountCreditedId() { return accountCreditedId; }
    public void setAccountCreditedId(String accountCreditedId) { this.accountCreditedId = accountCreditedId; }
}