package com.collectivity.repository;

import com.collectivity.datasource.DataSource;
import com.collectivity.entity.MemberPaymentEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MemberPaymentRepository {
    private final DataSource dataSource;

    public MemberPaymentRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public MemberPaymentEntity save(MemberPaymentEntity payment) {
        dataSource.saveMemberPayment(payment);
        return payment;
    }

    public Optional<MemberPaymentEntity> findById(String id) {
        return dataSource.findMemberPaymentById(id);
    }
}