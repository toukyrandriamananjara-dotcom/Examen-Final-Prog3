package com.collectivity.repository;

import com.collectivity.datasource.DataSource;
import com.collectivity.entity.CollectivityEntity;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public class CollectivityRepository {

    private final DataSource dataSource;

    public CollectivityRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public CollectivityEntity save(CollectivityEntity collectivity) {
        dataSource.saveCollectivity(collectivity);
        return collectivity;
    }

    public Optional<CollectivityEntity> findById(String id) {
        return dataSource.findCollectivityById(id);
    }

    public Collection<CollectivityEntity> findAll() {
        return dataSource.findAllCollectivities();
    }
}