package com.collectivity.repository;

import com.collectivity.datasource.DataSource;
import com.collectivity.entity.MembershipFeeEntity;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public class MembershipFeeRepository {
    private final DataSource dataSource;

    public MembershipFeeRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public MembershipFeeEntity save(MembershipFeeEntity fee) {
        dataSource.saveMembershipFee(fee);
        return fee;
    }

    public Optional<MembershipFeeEntity> findById(String id) {
        return dataSource.findMembershipFeeById(id);
    }

    public Collection<MembershipFeeEntity> findByCollectivityId(String collectivityId) {
        return dataSource.findMembershipFeesByCollectivityId(collectivityId);
    }
}