package com.collectivity.repository;

import com.collectivity.datasource.JdbcDataSource;
import com.collectivity.entity.CollectivityTransactionEntity;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public class TransactionRepository {

    private final JdbcDataSource jdbcDataSource;

    public TransactionRepository(JdbcDataSource jdbcDataSource) {
        this.jdbcDataSource = jdbcDataSource;
    }

    public CollectivityTransactionEntity save(CollectivityTransactionEntity transaction) {
        String sql = "INSERT INTO collectivity_transactions (id, collectivity_id, member_id, membership_fee_id, creation_date, amount, payment_mode, account_credited_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET collectivity_id = EXCLUDED.collectivity_id, member_id = EXCLUDED.member_id, membership_fee_id = EXCLUDED.membership_fee_id, creation_date = EXCLUDED.creation_date, amount = EXCLUDED.amount, payment_mode = EXCLUDED.payment_mode, account_credited_id = EXCLUDED.account_credited_id";
        try (Connection conn = jdbcDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (transaction.getId() == null) {
                transaction.setId(UUID.randomUUID().toString());
            }
            stmt.setString(1, transaction.getId());
            stmt.setString(2, transaction.getCollectivityId());
            stmt.setString(3, transaction.getMemberId());
            stmt.setString(4, transaction.getMembershipFeeId());
            stmt.setDate(5, Date.valueOf(transaction.getCreationDate()));
            stmt.setDouble(6, transaction.getAmount());
            stmt.setString(7, transaction.getPaymentMode());
            stmt.setString(8, transaction.getAccountCreditedId());
            stmt.executeUpdate();
            return transaction;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving transaction", e);
        }
    }

    public Collection<CollectivityTransactionEntity> findByCollectivityIdAndDateRange(String collectivityId, LocalDate from, LocalDate to) {
        String sql = "SELECT * FROM collectivity_transactions WHERE collectivity_id = ? AND creation_date BETWEEN ? AND ? ORDER BY creation_date";
        List<CollectivityTransactionEntity> list = new ArrayList<>();
        try (Connection conn = jdbcDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, collectivityId);
            stmt.setDate(2, Date.valueOf(from));
            stmt.setDate(3, Date.valueOf(to));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding transactions by collectivity and date range", e);
        }
    }

    public List<CollectivityTransactionEntity> findByAccountCreditedIdAndDateUpTo(String accountCreditedId, LocalDate upToDate) {
        String sql = "SELECT * FROM collectivity_transactions WHERE account_credited_id = ? AND creation_date <= ? ORDER BY creation_date";
        List<CollectivityTransactionEntity> list = new ArrayList<>();
        try (Connection conn = jdbcDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountCreditedId);
            stmt.setDate(2, Date.valueOf(upToDate));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding transactions by account", e);
        }
    }

    private CollectivityTransactionEntity mapResultSet(ResultSet rs) throws SQLException {
        CollectivityTransactionEntity t = new CollectivityTransactionEntity();
        t.setId(rs.getString("id"));
        t.setCollectivityId(rs.getString("collectivity_id"));
        t.setMemberId(rs.getString("member_id"));
        t.setMembershipFeeId(rs.getString("membership_fee_id"));
        t.setCreationDate(rs.getDate("creation_date").toLocalDate());
        t.setAmount(rs.getDouble("amount"));
        t.setPaymentMode(rs.getString("payment_mode"));
        t.setAccountCreditedId(rs.getString("account_credited_id"));
        return t;
    }
}