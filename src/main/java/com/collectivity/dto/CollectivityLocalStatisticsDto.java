package com.collectivity.dto;

public class CollectivityLocalStatisticsDto {
    private MemberDescriptionDto memberDescription;
    private double earnedAmount;
    private double unpaidAmount;

    // Getters and setters
    public MemberDescriptionDto getMemberDescription() { return memberDescription; }
    public void setMemberDescription(MemberDescriptionDto memberDescription) { this.memberDescription = memberDescription; }
    public double getEarnedAmount() { return earnedAmount; }
    public void setEarnedAmount(double earnedAmount) { this.earnedAmount = earnedAmount; }
    public double getUnpaidAmount() { return unpaidAmount; }
    public void setUnpaidAmount(double unpaidAmount) { this.unpaidAmount = unpaidAmount; }
}