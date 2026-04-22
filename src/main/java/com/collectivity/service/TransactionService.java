package com.collectivity.service;

import com.collectivity.dto.*;
import com.collectivity.entity.*;
import com.collectivity.exception.BadRequestException;
import com.collectivity.exception.NotFoundException;
import com.collectivity.repository.CollectivityRepository;
import com.collectivity.repository.FinancialAccountRepository;
import com.collectivity.repository.MemberRepository;
import com.collectivity.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CollectivityRepository collectivityRepository;
    private final MemberRepository memberRepository;
    private final FinancialAccountRepository financialAccountRepository;
    private final MemberService memberService;

    public TransactionService(TransactionRepository transactionRepository,
                              CollectivityRepository collectivityRepository,
                              MemberRepository memberRepository,
                              FinancialAccountRepository financialAccountRepository,
                              MemberService memberService) {
        this.transactionRepository = transactionRepository;
        this.collectivityRepository = collectivityRepository;
        this.memberRepository = memberRepository;
        this.financialAccountRepository = financialAccountRepository;
        this.memberService = memberService;
    }

    public List<CollectivityTransactionDto> getTransactionsByCollectivityAndDateRange(
            String collectivityId, LocalDate from, LocalDate to) {

        // Validate collectivity exists
        CollectivityEntity collectivity = collectivityRepository.findById(collectivityId)
                .orElseThrow(() -> new NotFoundException("Collectivity not found: " + collectivityId));

        // Validate date range
        if (from == null || to == null) {
            throw new BadRequestException("Both 'from' and 'to' query parameters are required");
        }

        if (from.isAfter(to)) {
            throw new BadRequestException("'from' date cannot be after 'to' date");
        }

        return transactionRepository.findByCollectivityIdAndDateRange(collectivityId, from, to).stream()
                .map(this::toDto)
                .toList();
    }

    private CollectivityTransactionDto toDto(CollectivityTransactionEntity entity) {
        CollectivityTransactionDto dto = new CollectivityTransactionDto();
        dto.setId(entity.getId());
        dto.setCreationDate(entity.getCreationDate());
        dto.setAmount(entity.getAmount());
        dto.setPaymentMode(entity.getPaymentMode());
        dto.setCollectivityId(entity.getCollectivityId());
        dto.setMembershipFeeId(entity.getMembershipFeeId());

        // Get member
        MemberEntity member = memberRepository.findById(entity.getMemberId())
                .orElse(null);
        if (member != null) {
            dto.setMemberDebited(memberService.toDto(member));
        }

        // Get financial account
        FinancialAccountEntity account = financialAccountRepository.findById(entity.getAccountCreditedId())
                .orElse(null);
        if (account != null) {
            dto.setAccountCredited(toFinancialAccountDto(account));
        }

        return dto;
    }

    private FinancialAccountDto toFinancialAccountDto(FinancialAccountEntity account) {
        if (account instanceof CashAccountEntity) {
            CashAccountDto dto = new CashAccountDto();
            dto.setId(account.getId());
            dto.setAmount(account.getAmount());
            dto.setCollectivityId(account.getCollectivityId());
            return dto;
        } else if (account instanceof MobileBankingAccountEntity) {
            MobileBankingAccountDto dto = new MobileBankingAccountDto();
            MobileBankingAccountEntity mobile = (MobileBankingAccountEntity) account;
            dto.setId(mobile.getId());
            dto.setAmount(mobile.getAmount());
            dto.setHolderName(mobile.getHolderName());
            dto.setMobileBankingService(mobile.getMobileBankingService());
            dto.setMobileNumber(mobile.getMobileNumber());
            return dto;
        } else if (account instanceof BankAccountEntity) {
            BankAccountDto dto = new BankAccountDto();
            BankAccountEntity bank = (BankAccountEntity) account;
            dto.setId(bank.getId());
            dto.setAmount(bank.getAmount());
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