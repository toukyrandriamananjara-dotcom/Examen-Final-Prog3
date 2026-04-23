package com.collectivity.repository;

import com.collectivity.datasource.JdbcDataSource;
import com.collectivity.entity.*;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;

@Repository
public class FinancialAccountRepository {

    private final JdbcDataSource jdbcDataSource;

    public FinancialAccountRepository(JdbcDataSource jdbcDataSource) {
        this.jdbcDataSource = jdbcDataSource;
    }

    public FinancialAccountEntity save(FinancialAccountEntity account) {
        String sql = "INSERT INTO financial_accounts (id, collectivity_id, account_type, amount, holder_name, mobile_banking_service, mobile_number, bank_name, bank_code, bank_branch_code, bank_account_number, bank_account_key) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET collectivity_id = EXCLUDED.collectivity_id, amount = EXCLUDED.amount, holder_name = EXCLUDED.holder_name, mobile_banking_service = EXCLUDED.mobile_banking_service, mobile_number = EXCLUDED.mobile_number, bank_name = EXCLUDED.bank_name, bank_code = EXCLUDED.bank_code, bank_branch_code = EXCLUDED.bank_branch_code, bank_account_number = EXCLUDED.bank_account_number, bank_account_key = EXCLUDED.bank_account_key";
        try (Connection conn = jdbcDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, account.getId());
            stmt.setString(2, account.getCollectivityId());
            stmt.setString(3, account.getAccountType());
            stmt.setDouble(4, account.getAmount());
            // Champs spécifiques
            if (account instanceof MobileBankingAccountEntity) {
                MobileBankingAccountEntity m = (MobileBankingAccountEntity) account;
                stmt.setString(5, m.getHolderName());
                stmt.setString(6, m.getMobileBankingService());
                stmt.setString(7, m.getMobileNumber());
                stmt.setNull(8, Types.VARCHAR);
                stmt.setNull(9, Types.VARCHAR);
                stmt.setNull(10, Types.VARCHAR);
                stmt.setNull(11, Types.VARCHAR);
                stmt.setNull(12, Types.VARCHAR);
            } else if (account instanceof BankAccountEntity) {
                BankAccountEntity b = (BankAccountEntity) account;
                stmt.setString(5, b.getHolderName());
                stmt.setNull(6, Types.VARCHAR);
                stmt.setNull(7, Types.VARCHAR);
                stmt.setString(8, b.getBankName());
                stmt.setString(9, b.getBankCode());
                stmt.setString(10, b.getBankBranchCode());
                stmt.setString(11, b.getBankAccountNumber());
                stmt.setString(12, b.getBankAccountKey());
            } else {
                stmt.setNull(5, Types.VARCHAR);
                stmt.setNull(6, Types.VARCHAR);
                stmt.setNull(7, Types.VARCHAR);
                stmt.setNull(8, Types.VARCHAR);
                stmt.setNull(9, Types.VARCHAR);
                stmt.setNull(10, Types.VARCHAR);
                stmt.setNull(11, Types.VARCHAR);
                stmt.setNull(12, Types.VARCHAR);
            }
            stmt.executeUpdate();
            return account;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving financial account", e);
        }
    }

    public Optional<FinancialAccountEntity> findById(String id) {
        String sql = "SELECT * FROM financial_accounts WHERE id = ?";
        try (Connection conn = jdbcDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSet(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding financial account", e);
        }
    }

    public Collection<FinancialAccountEntity> findByCollectivityId(String collectivityId) {
        String sql = "SELECT * FROM financial_accounts WHERE collectivity_id = ?";
        List<FinancialAccountEntity> list = new ArrayList<>();
        try (Connection conn = jdbcDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, collectivityId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding accounts by collectivity", e);
        }
    }

    public Optional<CashAccountEntity> findCashAccountByCollectivityId(String collectivityId) {
        String sql = "SELECT * FROM financial_accounts WHERE collectivity_id = ? AND account_type = 'CASH'";
        try (Connection conn = jdbcDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, collectivityId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of((CashAccountEntity) mapResultSet(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding cash account", e);
        }
    }

    private FinancialAccountEntity mapResultSet(ResultSet rs) throws SQLException {
        String type = rs.getString("account_type");
        FinancialAccountEntity acc;
        if ("CASH".equals(type)) {
            acc = new CashAccountEntity();
        } else if ("MOBILE_BANKING".equals(type)) {
            MobileBankingAccountEntity m = new MobileBankingAccountEntity();
            m.setHolderName(rs.getString("holder_name"));
            m.setMobileBankingService(rs.getString("mobile_banking_service"));
            m.setMobileNumber(rs.getString("mobile_number"));
            acc = m;
        } else if ("BANK_TRANSFER".equals(type)) {
            BankAccountEntity b = new BankAccountEntity();
            b.setHolderName(rs.getString("holder_name"));
            b.setBankName(rs.getString("bank_name"));
            b.setBankCode(rs.getString("bank_code"));
            b.setBankBranchCode(rs.getString("bank_branch_code"));
            b.setBankAccountNumber(rs.getString("bank_account_number"));
            b.setBankAccountKey(rs.getString("bank_account_key"));
            acc = b;
        } else {
            throw new IllegalArgumentException("Unknown account type: " + type);
        }
        acc.setId(rs.getString("id"));
        acc.setCollectivityId(rs.getString("collectivity_id"));
        acc.setAccountType(type);
        acc.setAmount(rs.getDouble("amount"));
        return acc;
    }
}