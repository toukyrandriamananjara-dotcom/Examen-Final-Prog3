package com.collectivity.service;

import com.collectivity.dto.*;
import com.collectivity.entity.*;
import com.collectivity.exception.BadRequestException;
import com.collectivity.exception.NotFoundException;
import com.collectivity.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MemberPaymentService {

    private final MemberRepository memberRepository;
    private final MembershipFeeRepository membershipFeeRepository;
    private final FinancialAccountRepository financialAccountRepository;
    private final MemberPaymentRepository memberPaymentRepository;
    private final TransactionRepository transactionRepository;
    private final MemberService memberService;

    // Percentage of periodic fees to be transferred to federation (to be configured)
    private static final double FEDERATION_PERCENTAGE = 10.0;

    public MemberPaymentService(MemberRepository memberRepository,
                                MembershipFeeRepository membershipFeeRepository,
                                FinancialAccountRepository financialAccountRepository,
                                MemberPaymentRepository memberPaymentRepository,
                                TransactionRepository transactionRepository,
                                MemberService memberService) {
        this.memberRepository = memberRepository;
        this.membershipFeeRepository = membershipFeeRepository;
        this.financialAccountRepository = financialAccountRepository;
        this.memberPaymentRepository = memberPaymentRepository;
        this.transactionRepository = transactionRepository;
        this.memberService = memberService;
    }

    @Transactional
    public List<MemberPaymentDto> createMemberPayments(String memberId, List<CreateMemberPaymentDto> requests) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("Member not found: " + memberId));

        List<MemberPaymentDto> created = new ArrayList<>();
        for (CreateMemberPaymentDto request : requests) {
            created.add(createSingleMemberPayment(member, request));
        }
        return created;
    }

    private MemberPaymentDto createSingleMemberPayment(MemberEntity member, CreateMemberPaymentDto request) {
        // Validate amount
        if (request.getAmount() <= 0) {
            throw new BadRequestException("Payment amount must be positive: " + request.getAmount());
        }

        // Get membership fee
        MembershipFeeEntity fee = membershipFeeRepository.findById(request.getMembershipFeeIdentifier())
                .orElseThrow(() -> new NotFoundException("Membership fee not found: " + request.getMembershipFeeIdentifier()));

        // Verify fee belongs to member's collectivity
        if (!fee.getCollectivityId().equals(member.getCollectivityId())) {
            throw new BadRequestException("Membership fee does not belong to member's collectivity");
        }

        // Validate payment amount matches expected amount
        if (Math.abs(request.getAmount() - fee.getAmount()) > 0.01) {
            throw new BadRequestException("Payment amount " + request.getAmount() +
                    " does not match expected amount " + fee.getAmount());
        }

        // Get financial account to credit
        FinancialAccountEntity accountCredited = financialAccountRepository.findById(request.getAccountCreditedIdentifier())
                .orElseThrow(() -> new NotFoundException("Financial account not found: " + request.getAccountCreditedIdentifier()));

        // Verify account belongs to member's collectivity
        if (!accountCredited.getCollectivityId().equals(member.getCollectivityId())) {
            throw new BadRequestException("Financial account does not belong to member's collectivity");
        }

        // For cash accounts, ensure only one exists per collectivity
        if (accountCredited instanceof CashAccountEntity) {
            financialAccountRepository.findCashAccountByCollectivityId(member.getCollectivityId())
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(accountCredited.getId())) {
                            throw new BadRequestException("Collectivity can only have one cash account");
                        }
                    });
        }

        // Validate payment mode matches account type
        validatePaymentMode(request.getPaymentMode(), accountCredited);

        // Create member payment record
        MemberPaymentEntity payment = new MemberPaymentEntity();
        payment.setId(UUID.randomUUID().toString());
        payment.setMemberId(member.getId());
        payment.setMembershipFeeId(fee.getId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMode(request.getPaymentMode());
        payment.setAccountCreditedId(accountCredited.getId());
        payment.setCreationDate(LocalDate.now());

        memberPaymentRepository.save(payment);

        // Update account balance
        double newBalance = accountCredited.getAmount() + request.getAmount();
        accountCredited.setAmount(newBalance);
        financialAccountRepository.save(accountCredited);

        // Create transaction record
        CollectivityTransactionEntity transaction = new CollectivityTransactionEntity();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setCollectivityId(member.getCollectivityId());
        transaction.setMemberId(member.getId());
        transaction.setMembershipFeeId(fee.getId());
        transaction.setCreationDate(LocalDate.now());
        transaction.setAmount(request.getAmount());
        transaction.setPaymentMode(request.getPaymentMode());
        transaction.setAccountCreditedId(accountCredited.getId());

        transactionRepository.save(transaction);

        // For periodic fees (monthly or annual), transfer percentage to federation
        if (fee.getFrequency().equals("MONTHLY") || fee.getFrequency().equals("ANNUALLY")) {
            transferToFederation(request.getAmount(), member.getCollectivityId());
        }

        return toDto(payment, accountCredited);
    }

    private void validatePaymentMode(String paymentMode, FinancialAccountEntity account) {
        if (paymentMode == null) {
            throw new BadRequestException("Payment mode is required");
        }

        switch (paymentMode) {
            case "CASH":
                if (!(account instanceof CashAccountEntity)) {
                    throw new BadRequestException("CASH payment mode requires a cash account");
                }
                break;
            case "MOBILE_BANKING":
                if (!(account instanceof MobileBankingAccountEntity)) {
                    throw new BadRequestException("MOBILE_BANKING payment mode requires a mobile banking account");
                }
                break;
            case "BANK_TRANSFER":
                if (!(account instanceof BankAccountEntity)) {
                    throw new BadRequestException("BANK_TRANSFER payment mode requires a bank account");
                }
                break;
            default:
                throw new BadRequestException("Unrecognized payment mode: " + paymentMode);
        }
    }

    private void transferToFederation(double amount, String collectivityId) {
        double federationAmount = amount * FEDERATION_PERCENTAGE / 100.0;
        double collectivityAmount = amount - federationAmount;


        System.out.println("Transfer to federation: " + federationAmount + " MGA from collectivity " + collectivityId);
        System.out.println("Remaining for collectivity: " + collectivityAmount + " MGA");
    }

    private MemberPaymentDto toDto(MemberPaymentEntity entity, FinancialAccountEntity account) {
        MemberPaymentDto dto = new MemberPaymentDto();
        dto.setId(entity.getId());
        dto.setAmount(entity.getAmount());
        dto.setPaymentMode(entity.getPaymentMode());
        dto.setCreationDate(entity.getCreationDate());
        dto.setMemberId(entity.getMemberId());
        dto.setMembershipFeeId(entity.getMembershipFeeId());

        // Set account credited DTO based on account type
        if (account instanceof CashAccountEntity) {
            CashAccountDto cashDto = new CashAccountDto();
            cashDto.setId(account.getId());
            cashDto.setAmount(account.getAmount());
            cashDto.setCollectivityId(account.getCollectivityId());
            dto.setAccountCredited(cashDto);
        } else if (account instanceof MobileBankingAccountEntity) {
            MobileBankingAccountDto mobileDto = new MobileBankingAccountDto();
            MobileBankingAccountEntity mobile = (MobileBankingAccountEntity) account;
            mobileDto.setId(mobile.getId());
            mobileDto.setAmount(mobile.getAmount());
            mobileDto.setHolderName(mobile.getHolderName());
            mobileDto.setMobileBankingService(mobile.getMobileBankingService());
            mobileDto.setMobileNumber(mobile.getMobileNumber());
            dto.setAccountCredited(mobileDto);
        } else if (account instanceof BankAccountEntity) {
            BankAccountDto bankDto = new BankAccountDto();
            BankAccountEntity bank = (BankAccountEntity) account;
            bankDto.setId(bank.getId());
            bankDto.setAmount(bank.getAmount());
            bankDto.setHolderName(bank.getHolderName());
            bankDto.setBankName(bank.getBankName());
            bankDto.setBankCode(bank.getBankCode());
            bankDto.setBankBranchCode(bank.getBankBranchCode());
            bankDto.setBankAccountNumber(bank.getBankAccountNumber());
            bankDto.setBankAccountKey(bank.getBankAccountKey());
            dto.setAccountCredited(bankDto);
        }

        return dto;
    }
}