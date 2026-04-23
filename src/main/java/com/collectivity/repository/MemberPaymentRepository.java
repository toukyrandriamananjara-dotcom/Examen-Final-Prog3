package com.collectivity.repository;

import com.collectivity.datasource.JdbcDataSource;
import com.collectivity.entity.MemberPaymentEntity;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

@Repository
public class MemberPaymentRepository {

    private final JdbcDataSource jdbcDataSource;

    public MemberPaymentRepository(JdbcDataSource jdbcDataSource) {
        this.jdbcDataSource = jdbcDataSource;
    }

    public MemberPaymentEntity save(MemberPaymentEntity payment) {
        String sql = "INSERT INTO member_payments (id, member_id, membership_fee_id, amount, payment_mode, account_credited_id, creation_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET member_id = EXCLUDED.member_id, membership_fee_id = EXCLUDED.membership_fee_id, amount = EXCLUDED.amount, payment_mode = EXCLUDED.payment_mode, account_credited_id = EXCLUDED.account_credited_id, creation_date = EXCLUDED.creation_date";
        try (Connection conn = jdbcDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (payment.getId() == null) {
                payment.setId(UUID.randomUUID().toString());
            }
            stmt.setString(1, payment.getId());
            stmt.setString(2, payment.getMemberId());
            stmt.setString(3, payment.getMembershipFeeId());
            stmt.setDouble(4, payment.getAmount());
            stmt.setString(5, payment.getPaymentMode());
            stmt.setString(6, payment.getAccountCreditedId());
            stmt.setDate(7, Date.valueOf(payment.getCreationDate()));
            stmt.executeUpdate();
            return payment;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving member payment", e);
        }
    }

    public Optional<MemberPaymentEntity> findById(String id) {
        String sql = "SELECT * FROM member_payments WHERE id = ?";
        try (Connection conn = jdbcDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSet(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding member payment", e);
        }
    }

    private MemberPaymentEntity mapResultSet(ResultSet rs) throws SQLException {
        MemberPaymentEntity p = new MemberPaymentEntity();
        p.setId(rs.getString("id"));
        p.setMemberId(rs.getString("member_id"));
        p.setMembershipFeeId(rs.getString("membership_fee_id"));
        p.setAmount(rs.getDouble("amount"));
        p.setPaymentMode(rs.getString("payment_mode"));
        p.setAccountCreditedId(rs.getString("account_credited_id"));
        p.setCreationDate(rs.getDate("creation_date").toLocalDate());
        return p;
    }
}