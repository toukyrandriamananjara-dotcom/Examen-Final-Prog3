package com.collectivity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public class CollectivityTransactionDto {
    private String id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate creationDate;

    private Double amount;
    private String paymentMode;
    private FinancialAccountDto accountCredited;
    private MemberDto memberDebited;
    private String collectivityId;
    private String membershipFeeId;

    public CollectivityTransactionDto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public LocalDate getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDate creationDate) { this.creationDate = creationDate; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }

    public FinancialAccountDto getAccountCredited() { return accountCredited; }
    public void setAccountCredited(FinancialAccountDto accountCredited) { this.accountCredited = accountCredited; }

    public MemberDto getMemberDebited() { return memberDebited; }
    public void setMemberDebited(MemberDto memberDebited) { this.memberDebited = memberDebited; }

    public String getCollectivityId() { return collectivityId; }
    public void setCollectivityId(String collectivityId) { this.collectivityId = collectivityId; }

    public String getMembershipFeeId() { return membershipFeeId; }
    public void setMembershipFeeId(String membershipFeeId) { this.membershipFeeId = membershipFeeId; }
}