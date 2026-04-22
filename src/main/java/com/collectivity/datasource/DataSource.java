package com.collectivity.datasource;

import com.collectivity.entity.CollectivityEntity;
import com.collectivity.entity.MemberEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory store replacing a database.
 * Uses ConcurrentHashMap for basic thread safety.
 */
@Component
public class DataSource {

    private final Map<String, MemberEntity> members = new ConcurrentHashMap<>();
    private final Map<String, CollectivityEntity> collectivities = new ConcurrentHashMap<>();

    // ── Members ──────────────────────────────────────────────────────────────

    public void saveMember(MemberEntity member) {
        members.put(member.getId(), member);
    }

    public Optional<MemberEntity> findMemberById(String id) {
        return Optional.ofNullable(members.get(id));
    }

    public Collection<MemberEntity> findAllMembers() {
        return members.values();
    }

    // ── Collectivities ────────────────────────────────────────────────────────

    public void saveCollectivity(CollectivityEntity collectivity) {
        collectivities.put(collectivity.getId(), collectivity);
    }

    public Optional<CollectivityEntity> findCollectivityById(String id) {
        return Optional.ofNullable(collectivities.get(id));
    }

    public Collection<CollectivityEntity> findAllCollectivities() {
        return collectivities.values();
    }
}