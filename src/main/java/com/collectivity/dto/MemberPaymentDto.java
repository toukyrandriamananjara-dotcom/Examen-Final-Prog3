package com.collectivity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public class MemberPaymentDto {
    private String id;
    private Double amount;
    private String paymentMode;
    private FinancialAccountDto accountCredited;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate creationDate;

    private String memberId;
    private String membershipFeeId;

    public MemberPaymentDto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }

    public FinancialAccountDto getAccountCredited() { return accountCredited; }
    public void setAccountCredited(FinancialAccountDto accountCredited) { this.accountCredited = accountCredited; }

    public LocalDate getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDate creationDate) { this.creationDate = creationDate; }

    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }

    public String getMembershipFeeId() { return membershipFeeId; }
    public void setMembershipFeeId(String membershipFeeId) { this.membershipFeeId = membershipFeeId; }
}