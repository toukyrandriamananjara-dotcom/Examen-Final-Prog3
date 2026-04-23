package com.collectivity.repository;

import com.collectivity.datasource.JdbcDataSource;
import com.collectivity.entity.MembershipFeeEntity;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.sql.Date;
import java.util.*;

@Repository
public class MembershipFeeRepository {

    private final JdbcDataSource jdbcDataSource;

    public MembershipFeeRepository(JdbcDataSource jdbcDataSource) {
        this.jdbcDataSource = jdbcDataSource;
    }

    public MembershipFeeEntity save(MembershipFeeEntity fee) {
        String sql = "INSERT INTO membership_fees (id, collectivity_id, eligible_from, frequency, amount, label, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET collectivity_id = EXCLUDED.collectivity_id, eligible_from = EXCLUDED.eligible_from, frequency = EXCLUDED.frequency, amount = EXCLUDED.amount, label = EXCLUDED.label, status = EXCLUDED.status";
        try (Connection conn = jdbcDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fee.getId());
            stmt.setString(2, fee.getCollectivityId());
            stmt.setDate(3, Date.valueOf(fee.getEligibleFrom()));
            stmt.setString(4, fee.getFrequency());
            stmt.setDouble(5, fee.getAmount());
            stmt.setString(6, fee.getLabel());
            stmt.setString(7, fee.getStatus());
            stmt.executeUpdate();
            return fee;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving membership fee", e);
        }
    }

    public Optional<MembershipFeeEntity> findById(String id) {
        String sql = "SELECT * FROM membership_fees WHERE id = ?";
        try (Connection conn = jdbcDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSet(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding membership fee", e);
        }
    }

    public Collection<MembershipFeeEntity> findByCollectivityId(String collectivityId) {
        String sql = "SELECT * FROM membership_fees WHERE collectivity_id = ?";
        List<MembershipFeeEntity> list = new ArrayList<>();
        try (Connection conn = jdbcDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, collectivityId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding fees by collectivity", e);
        }
    }

    private MembershipFeeEntity mapResultSet(ResultSet rs) throws SQLException {
        MembershipFeeEntity f = new MembershipFeeEntity();
        f.setId(rs.getString("id"));
        f.setCollectivityId(rs.getString("collectivity_id"));
        f.setEligibleFrom(rs.getDate("eligible_from").toLocalDate());
        f.setFrequency(rs.getString("frequency"));
        f.setAmount(rs.getDouble("amount"));
        f.setLabel(rs.getString("label"));
        f.setStatus(rs.getString("status"));
        return f;
    }
}