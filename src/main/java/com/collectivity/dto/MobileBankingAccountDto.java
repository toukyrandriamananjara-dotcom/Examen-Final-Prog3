package com.collectivity.dto;

public class MobileBankingAccountDto extends FinancialAccountDto {
    private String holderName;
    private String mobileBankingService;
    private String mobileNumber;

    public MobileBankingAccountDto() {}

    public String getHolderName() { return holderName; }
    public void setHolderName(String holderName) { this.holderName = holderName; }

    public String getMobileBankingService() { return mobileBankingService; }
    public void setMobileBankingService(String mobileBankingService) { this.mobileBankingService = mobileBankingService; }

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
}