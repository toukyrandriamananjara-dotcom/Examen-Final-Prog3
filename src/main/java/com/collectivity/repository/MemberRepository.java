package com.collectivity.repository;

import com.collectivity.datasource.JdbcDataSource;
import com.collectivity.entity.MemberEntity;
import com.collectivity.entity.Gender;
import com.collectivity.entity.MemberOccupation;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.sql.Date;
import java.util.*;

@Repository
public class MemberRepository {

    private final JdbcDataSource jdbcDataSource;

    public MemberRepository(JdbcDataSource jdbcDataSource) {
        this.jdbcDataSource = jdbcDataSource;
    }

    public MemberEntity save(MemberEntity member) {
        String sql = "INSERT INTO members (id, first_name, last_name, birth_date, gender, address, profession, phone_number, email, occupation, collectivity_id, membership_date, annual_contribution_amount) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET first_name = EXCLUDED.first_name, last_name = EXCLUDED.last_name, birth_date = EXCLUDED.birth_date, gender = EXCLUDED.gender, address = EXCLUDED.address, profession = EXCLUDED.profession, phone_number = EXCLUDED.phone_number, email = EXCLUDED.email, occupation = EXCLUDED.occupation, collectivity_id = EXCLUDED.collectivity_id, membership_date = EXCLUDED.membership_date, annual_contribution_amount = EXCLUDED.annual_contribution_amount";
        try (Connection conn = jdbcDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, member.getId());
            stmt.setString(2, member.getFirstName());
            stmt.setString(3, member.getLastName());
            stmt.setDate(4, Date.valueOf(member.getBirthDate()));
            stmt.setString(5, member.getGender().name());
            stmt.setString(6, member.getAddress());
            stmt.setString(7, member.getProfession());
            if (member.getPhoneNumber() != null) {
                stmt.setLong(8, member.getPhoneNumber());
            } else {
                stmt.setNull(8, Types.BIGINT);
            }
            stmt.setString(9, member.getEmail());
            stmt.setString(10, member.getOccupation().name());
            stmt.setString(11, member.getCollectivityId());
            stmt.setDate(12, Date.valueOf(member.getMembershipDate()));
            if (member.getAnnualContributionAmount() != null) {
                stmt.setLong(13, member.getAnnualContributionAmount());
            } else {
                stmt.setNull(13, Types.BIGINT);
            }
            stmt.executeUpdate();
            return member;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving member", e);
        }
    }

    public Optional<MemberEntity> findById(String id) {
        String sql = "SELECT * FROM members WHERE id = ?";
        try (Connection conn = jdbcDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSet(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding member by id", e);
        }
    }

    public Collection<MemberEntity> findAll() {
        String sql = "SELECT * FROM members";
        List<MemberEntity> list = new ArrayList<>();
        try (Connection conn = jdbcDataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all members", e);
        }
    }

    public List<MemberEntity> findByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = "SELECT * FROM members WHERE id IN (" + placeholders + ")";
        List<MemberEntity> list = new ArrayList<>();
        try (Connection conn = jdbcDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < ids.size(); i++) {
                stmt.setString(i+1, ids.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding members by ids", e);
        }
    }

    public List<String> findRefereeIdsByMemberId(String memberId) {
        String sql = "SELECT referee_id FROM member_referees WHERE member_id = ?";
        List<String> refereeIds = new ArrayList<>();
        try (Connection conn = jdbcDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, memberId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                refereeIds.add(rs.getString("referee_id"));
            }
            return refereeIds;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding referee ids", e);
        }
    }

    public Map<String, String> findRefereeRelationsByMemberId(String memberId) {
        String sql = "SELECT referee_id, relation_type FROM member_referees WHERE member_id = ?";
        Map<String, String> map = new HashMap<>();
        try (Connection conn = jdbcDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, memberId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                map.put(rs.getString("referee_id"), rs.getString("relation_type"));
            }
            return map;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding referee relations", e);
        }
    }

    public void saveRefereeRelation(String memberId, String refereeId, String relationType) {
        String sql = "INSERT INTO member_referees (member_id, referee_id, relation_type) VALUES (?, ?, ?) ON CONFLICT DO NOTHING";
        try (Connection conn = jdbcDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, memberId);
            stmt.setString(2, refereeId);
            stmt.setString(3, relationType);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving referee relation", e);
        }
    }

    private MemberEntity mapResultSet(ResultSet rs) throws SQLException {
        MemberEntity m = new MemberEntity();
        m.setId(rs.getString("id"));
        m.setFirstName(rs.getString("first_name"));
        m.setLastName(rs.getString("last_name"));
        m.setBirthDate(rs.getDate("birth_date").toLocalDate());
        m.setGender(Gender.valueOf(rs.getString("gender")));
        m.setAddress(rs.getString("address"));
        m.setProfession(rs.getString("profession"));
        m.setPhoneNumber(rs.getObject("phone_number") != null ? rs.getLong("phone_number") : null);
        m.setEmail(rs.getString("email"));
        m.setOccupation(MemberOccupation.valueOf(rs.getString("occupation")));
        m.setCollectivityId(rs.getString("collectivity_id"));
        m.setMembershipDate(rs.getDate("membership_date").toLocalDate());
        m.setAnnualContributionAmount(rs.getObject("annual_contribution_amount") != null ? rs.getLong("annual_contribution_amount") : null);
        return m;
    }
}