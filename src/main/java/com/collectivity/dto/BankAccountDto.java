package com.collectivity.dto;

public class BankAccountDto extends FinancialAccountDto {
    private String holderName;
    private String bankName;
    private String bankCode;
    private String bankBranchCode;
    private String bankAccountNumber;
    private String bankAccountKey;

    public BankAccountDto() {}

    public String getHolderName() { return holderName; }
    public void setHolderName(String holderName) { this.holderName = holderName; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getBankCode() { return bankCode; }
    public void setBankCode(String bankCode) { this.bankCode = bankCode; }

    public String getBankBranchCode() { return bankBranchCode; }
    public void setBankBranchCode(String bankBranchCode) { this.bankBranchCode = bankBranchCode; }

    public String getBankAccountNumber() { return bankAccountNumber; }
    public void setBankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; }

    public String getBankAccountKey() { return bankAccountKey; }
    public void setBankAccountKey(String bankAccountKey) { this.bankAccountKey = bankAccountKey; }
}
