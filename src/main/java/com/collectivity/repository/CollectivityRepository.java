package com.collectivity.repository;

import com.collectivity.datasource.JdbcDataSource;
import com.collectivity.entity.CollectivityEntity;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class CollectivityRepository {

    private final JdbcDataSource dataSource;

    public CollectivityRepository(JdbcDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public CollectivityEntity save(CollectivityEntity collectivity) {
        String sql = "INSERT INTO collectivities (id, number, name, location, agricultural_specialty, creation_date, federation_approval, president_id, deputy_president_id, treasurer_id, secretary_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET number = EXCLUDED.number, name = EXCLUDED.name, location = EXCLUDED.location, agricultural_specialty = EXCLUDED.agricultural_specialty, president_id = EXCLUDED.president_id, deputy_president_id = EXCLUDED.deputy_president_id, treasurer_id = EXCLUDED.treasurer_id, secretary_id = EXCLUDED.secretary_id";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, collectivity.getId());
            stmt.setString(2, collectivity.getNumber());
            stmt.setString(3, collectivity.getName());
            stmt.setString(4, collectivity.getLocation());
            stmt.setString(5, collectivity.getAgriculturalSpecialty());
            stmt.setDate(6, Date.valueOf(collectivity.getCreationDate()));
            stmt.setBoolean(7, collectivity.isFederationApproval());
            stmt.setString(8, collectivity.getPresidentId());
            stmt.setString(9, collectivity.getDeputyPresidentId());
            stmt.setString(10, collectivity.getTreasurerId());
            stmt.setString(11, collectivity.getSecretaryId());
            stmt.executeUpdate();
            return collectivity;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving collectivity", e);
        }
    }

    public Optional<CollectivityEntity> findById(String id) {
        String sql = "SELECT * FROM collectivities WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSet(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding collectivity", e);
        }
    }

    public Collection<CollectivityEntity> findAll() {
        String sql = "SELECT * FROM collectivities";
        List<CollectivityEntity> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all collectivities", e);
        }
    }

    // Méthode utilitaire pour mapper un ResultSet vers CollectivityEntity
    private CollectivityEntity mapResultSet(ResultSet rs) throws SQLException {
        CollectivityEntity entity = new CollectivityEntity();
        entity.setId(rs.getString("id"));
        entity.setNumber(rs.getString("number"));
        entity.setName(rs.getString("name"));
        entity.setLocation(rs.getString("location"));
        entity.setAgriculturalSpecialty(rs.getString("agricultural_specialty"));
        entity.setCreationDate(rs.getDate("creation_date").toLocalDate());
        entity.setFederationApproval(rs.getBoolean("federation_approval"));
        entity.setPresidentId(rs.getString("president_id"));
        entity.setDeputyPresidentId(rs.getString("deputy_president_id"));
        entity.setTreasurerId(rs.getString("treasurer_id"));
        entity.setSecretaryId(rs.getString("secretary_id"));
        // annual_contribution_amount n'est pas dans votre table? Si oui, ajoutez-le.
        return entity;
    }
}