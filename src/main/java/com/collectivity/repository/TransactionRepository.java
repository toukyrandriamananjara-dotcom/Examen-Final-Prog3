package com.collectivity.repository;

import com.collectivity.datasource.DataSource;
import com.collectivity.entity.CollectivityTransactionEntity;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;

@Repository
public class TransactionRepository {
    private final DataSource dataSource;

    public TransactionRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public CollectivityTransactionEntity save(CollectivityTransactionEntity transaction) {
        dataSource.saveTransaction(transaction);
        return transaction;
    }

    public Collection<CollectivityTransactionEntity> findByCollectivityIdAndDateRange(
            String collectivityId, LocalDate from, LocalDate to) {
        return dataSource.findTransactionsByCollectivityIdAndDateRange(collectivityId, from, to);
    }
}