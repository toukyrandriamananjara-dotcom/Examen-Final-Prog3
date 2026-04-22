package com.collectivity.repository;

import com.collectivity.datasource.DataSource;
import com.collectivity.entity.MemberEntity;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public class MemberRepository {

    private final DataSource dataSource;

    public MemberRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public MemberEntity save(MemberEntity member) {
        dataSource.saveMember(member);
        return member;
    }

    public Optional<MemberEntity> findById(String id) {
        return dataSource.findMemberById(id);
    }

    public Collection<MemberEntity> findAll() {
        return dataSource.findAllMembers();
    }
}