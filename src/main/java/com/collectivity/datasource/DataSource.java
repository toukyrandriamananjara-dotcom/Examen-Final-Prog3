package com.collectivity.datasource;

import com.collectivity.entity.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DataSource {

    private final Map<String, MemberEntity> members = new ConcurrentHashMap<>();
    private final Map<String, CollectivityEntity> collectivities = new ConcurrentHashMap<>();

    private final Map<String, MembershipFeeEntity> membershipFees = new ConcurrentHashMap<>();
    private final Map<String, FinancialAccountEntity> financialAccounts = new ConcurrentHashMap<>();
    private final Map<String, MemberPaymentEntity> memberPayments = new ConcurrentHashMap<>();
    private final Map<String, CollectivityTransactionEntity> transactions = new ConcurrentHashMap<>();

    public void saveMember(MemberEntity member) { members.put(member.getId(), member); }
    public Optional<MemberEntity> findMemberById(String id) { return Optional.ofNullable(members.get(id)); }
    public Collection<MemberEntity> findAllMembers() { return members.values(); }

    public void saveCollectivity(CollectivityEntity collectivity) { collectivities.put(collectivity.getId(), collectivity); }
    public Optional<CollectivityEntity> findCollectivityById(String id) { return Optional.ofNullable(collectivities.get(id)); }
    public Collection<CollectivityEntity> findAllCollectivities() { return collectivities.values(); }

    public void saveMembershipFee(MembershipFeeEntity fee) { membershipFees.put(fee.getId(), fee); }
    public Optional<MembershipFeeEntity> findMembershipFeeById(String id) { return Optional.ofNullable(membershipFees.get(id)); }
    public Collection<MembershipFeeEntity> findMembershipFeesByCollectivityId(String collectivityId) {
        return membershipFees.values().stream()
                .filter(fee -> fee.getCollectivityId().equals(collectivityId))
                .toList();
    }

    public void saveFinancialAccount(FinancialAccountEntity account) { financialAccounts.put(account.getId(), account); }
    public Optional<FinancialAccountEntity> findFinancialAccountById(String id) { return Optional.ofNullable(financialAccounts.get(id)); }
    public Collection<FinancialAccountEntity> findFinancialAccountsByCollectivityId(String collectivityId) {
        return financialAccounts.values().stream()
                .filter(account -> account.getCollectivityId().equals(collectivityId))
                .toList();
    }

    public Optional<CashAccountEntity> findCashAccountByCollectivityId(String collectivityId) {
        return financialAccounts.values().stream()
                .filter(account -> account instanceof CashAccountEntity)
                .map(account -> (CashAccountEntity) account)
                .filter(account -> account.getCollectivityId().equals(collectivityId))
                .findFirst();
    }

    public void saveMemberPayment(MemberPaymentEntity payment) { memberPayments.put(payment.getId(), payment); }
    public Optional<MemberPaymentEntity> findMemberPaymentById(String id) { return Optional.ofNullable(memberPayments.get(id)); }
    public Collection<MemberPaymentEntity> findMemberPaymentsByMemberId(String memberId) {
        return memberPayments.values().stream()
                .filter(payment -> payment.getMemberId().equals(memberId))
                .toList();
    }

    public void saveTransaction(CollectivityTransactionEntity transaction) { transactions.put(transaction.getId(), transaction); }
    public Collection<CollectivityTransactionEntity> findTransactionsByCollectivityIdAndDateRange(
            String collectivityId, java.time.LocalDate from, java.time.LocalDate to) {
        return transactions.values().stream()
                .filter(tx -> tx.getCollectivityId().equals(collectivityId))
                .filter(tx -> !tx.getCreationDate().isBefore(from) && !tx.getCreationDate().isAfter(to))
                .toList();
    }

    public Collection<CollectivityTransactionEntity> findTransactionsByAccountCreditedIdAndDateUpTo(
            String accountCreditedId, LocalDate upToDate) {
        return transactions.values().stream()
                .filter(tx -> tx.getAccountCreditedId().equals(accountCreditedId))
                .filter(tx -> !tx.getCreationDate().isAfter(upToDate))
                .toList();
    }
}