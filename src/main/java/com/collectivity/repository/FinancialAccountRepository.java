package com.collectivity.repository;

import com.collectivity.datasource.DataSource;
import com.collectivity.entity.CashAccountEntity;
import com.collectivity.entity.FinancialAccountEntity;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public class FinancialAccountRepository {
    private final DataSource dataSource;

    public FinancialAccountRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public FinancialAccountEntity save(FinancialAccountEntity account) {
        dataSource.saveFinancialAccount(account);
        return account;
    }

    public Optional<FinancialAccountEntity> findById(String id) {
        return dataSource.findFinancialAccountById(id);
    }

    public Collection<FinancialAccountEntity> findByCollectivityId(String collectivityId) {
        return dataSource.findFinancialAccountsByCollectivityId(collectivityId);
    }

    public Optional<CashAccountEntity> findCashAccountByCollectivityId(String collectivityId) {
        return dataSource.findCashAccountByCollectivityId(collectivityId);
    }
}