package com.collectivity.service;

import com.collectivity.dto.*;
import com.collectivity.entity.*;
import com.collectivity.exception.BadRequestException;
import com.collectivity.exception.NotFoundException;
import com.collectivity.repository.CollectivityRepository;
import com.collectivity.repository.FinancialAccountRepository;
import com.collectivity.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FinancialAccountService {

    private final FinancialAccountRepository financialAccountRepository;
    private final CollectivityRepository collectivityRepository;
    private final TransactionRepository transactionRepository;

    public FinancialAccountService(FinancialAccountRepository financialAccountRepository,
                                   CollectivityRepository collectivityRepository,
                                   TransactionRepository transactionRepository) {
        this.financialAccountRepository = financialAccountRepository;
        this.collectivityRepository = collectivityRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<FinancialAccountWithBalanceDto> getFinancialAccountsWithBalance(String collectivityId, LocalDate atDate) {
        // Validate collectivity exists
        CollectivityEntity collectivity = collectivityRepository.findById(collectivityId)
                .orElseThrow(() -> new NotFoundException("Collectivity not found: " + collectivityId));

        // Validate date parameter
        if (atDate == null) {
            throw new BadRequestException("Query parameter 'at' is required with format yyyy-MM-dd");
        }

        if (atDate.isAfter(LocalDate.now())) {
            throw new BadRequestException("Date cannot be in the future: " + atDate);
        }

        // Get all financial accounts for this collectivity
        List<FinancialAccountEntity> accounts = financialAccountRepository.findByCollectivityId(collectivityId).stream().toList();

        List<FinancialAccountWithBalanceDto> result = new ArrayList<>();
        for (FinancialAccountEntity account : accounts) {
            double balanceAtDate = calculateBalanceAtDate(account.getId(), atDate);
            result.add(toBalanceDto(account, balanceAtDate));
        }

        return result;
    }

    private double calculateBalanceAtDate(String accountId, LocalDate atDate) {
        List<CollectivityTransactionEntity> transactions = transactionRepository.findByAccountCreditedIdAndDateUpTo(accountId, atDate);

        double totalCredited = 0.0;
        for (CollectivityTransactionEntity tx : transactions) {
            totalCredited += tx.getAmount();
        }

        return totalCredited;
    }

    private FinancialAccountWithBalanceDto toBalanceDto(FinancialAccountEntity account, double balance) {
        if (account instanceof CashAccountEntity) {
            CashAccountWithBalanceDto dto = new CashAccountWithBalanceDto();
            dto.setId(account.getId());
            dto.setBalance(balance);
            return dto;
        } else if (account instanceof MobileBankingAccountEntity) {
            MobileBankingAccountWithBalanceDto dto = new MobileBankingAccountWithBalanceDto();
            MobileBankingAccountEntity mobile = (MobileBankingAccountEntity) account;
            dto.setId(mobile.getId());
            dto.setBalance(balance);
            dto.setHolderName(mobile.getHolderName());
            dto.setMobileBankingService(mobile.getMobileBankingService());
            dto.setMobileNumber(mobile.getMobileNumber());
            return dto;
        } else if (account instanceof BankAccountEntity) {
            BankAccountWithBalanceDto dto = new BankAccountWithBalanceDto();
            BankAccountEntity bank = (BankAccountEntity) account;
            dto.setId(bank.getId());
            dto.setBalance(balance);
            dto.setHolderName(bank.getHolderName());
            dto.setBankName(bank.getBankName());
            dto.setBankCode(bank.getBankCode());
            dto.setBankBranchCode(bank.getBankBranchCode());
            dto.setBankAccountNumber(bank.getBankAccountNumber());
            dto.setBankAccountKey(bank.getBankAccountKey());
            return dto;
        }
        return null;
    }
}