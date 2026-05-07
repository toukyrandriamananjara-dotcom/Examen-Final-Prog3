package com.collectivity.service;

import com.collectivity.dto.*;
import com.collectivity.entity.*;
import com.collectivity.exception.NotFoundException;
import com.collectivity.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class StatisticsService {

    private final CollectivityRepository collectivityRepository;
    private final MemberRepository memberRepository;
    private final MembershipFeeRepository membershipFeeRepository;
    private final MemberPaymentRepository memberPaymentRepository;

    public StatisticsService(CollectivityRepository collectivityRepository,
                             MemberRepository memberRepository,
                             MembershipFeeRepository membershipFeeRepository,
                             MemberPaymentRepository memberPaymentRepository) {
        this.collectivityRepository = collectivityRepository;
        this.memberRepository = memberRepository;
        this.membershipFeeRepository = membershipFeeRepository;
        this.memberPaymentRepository = memberPaymentRepository;
    }

    public List<CollectivityLocalStatisticsDto> getLocalStatistics(String collectivityId, LocalDate from, LocalDate to) {
        collectivityRepository.findById(collectivityId)
                .orElseThrow(() -> new NotFoundException("Collectivity not found: " + collectivityId));

        List<MemberEntity> members = memberRepository.findByCollectivityId(collectivityId);

        List<MembershipFeeEntity> activeFees = membershipFeeRepository.findByCollectivityIdAndStatus(collectivityId, "ACTIVE");

        List<MemberPaymentEntity> payments = memberPaymentRepository.findByCollectivityAndDateRange(collectivityId, from, to);

        Map<String, Double> earnedByMember = new HashMap<>();
        Map<String, Map<String, Double>> paidByMemberAndFee = new HashMap<>();

        for (MemberPaymentEntity p : payments) {
            String memberId = p.getMemberId();
            String feeId = p.getMembershipFeeId();
            double amount = p.getAmount();

            earnedByMember.merge(memberId, amount, Double::sum);

            paidByMemberAndFee
                    .computeIfAbsent(memberId, k -> new HashMap<>())
                    .merge(feeId, amount, Double::sum);
        }

        List<CollectivityLocalStatisticsDto> result = new ArrayList<>();

        for (MemberEntity member : members) {
            String memberId = member.getId();

            double earned = earnedByMember.getOrDefault(memberId, 0.0);
            double unpaid = 0.0;

            Map<String, Double> feePayments = paidByMemberAndFee.getOrDefault(memberId, Collections.emptyMap());

            for (MembershipFeeEntity fee : activeFees) {
                double expected = computeExpectedForFee(fee, from, to);
                double paid = feePayments.getOrDefault(fee.getId(), 0.0);
                if (expected > paid) {
                    unpaid += (expected - paid);
                }
            }

            MemberDescriptionDto desc = new MemberDescriptionDto();
            desc.setId(member.getId());
            desc.setFirstName(member.getFirstName());
            desc.setLastName(member.getLastName());
            desc.setEmail(member.getEmail());
            desc.setOccupation(member.getOccupation().name());

            CollectivityLocalStatisticsDto dto = new CollectivityLocalStatisticsDto();
            dto.setMemberDescription(desc);
            dto.setEarnedAmount(earned);
            dto.setUnpaidAmount(unpaid);
            result.add(dto);
        }

        return result;
    }

    public List<CollectivityOverallStatisticsDto> getOverallStatistics(LocalDate from, LocalDate to) {
        Collection<CollectivityEntity> collectivities = collectivityRepository.findAll();
        List<CollectivityOverallStatisticsDto> result = new ArrayList<>();

        for (CollectivityEntity coll : collectivities) {
            String collId = coll.getId();

            int newMembers = memberRepository.countByCollectivityIdAndMembershipDateBetween(collId, from, to);

            List<MemberEntity> members = memberRepository.findByCollectivityId(collId);
            List<MembershipFeeEntity> activeFees = membershipFeeRepository.findByCollectivityIdAndStatus(collId, "ACTIVE");
            List<MemberPaymentEntity> payments = memberPaymentRepository.findByCollectivityAndDateRange(collId, from, to);

            Map<String, Map<String, Double>> paidIndex = new HashMap<>();
            for (MemberPaymentEntity p : payments) {
                paidIndex
                        .computeIfAbsent(p.getMemberId(), k -> new HashMap<>())
                        .merge(p.getMembershipFeeId(), p.getAmount(), Double::sum);
            }

            int upToDateCount = 0;
            if (!members.isEmpty()) {
                for (MemberEntity member : members) {
                    boolean isUpToDate = true;
                    Map<String, Double> feePayments = paidIndex.getOrDefault(member.getId(), Collections.emptyMap());
                    for (MembershipFeeEntity fee : activeFees) {
                        double expected = computeExpectedForFee(fee, from, to);
                        double paid = feePayments.getOrDefault(fee.getId(), 0.0);
                        if (expected > paid) {
                            isUpToDate = false;
                            break;
                        }
                    }
                    if (isUpToDate) {
                        upToDateCount++;
                    }
                }
            }

            double percentage = members.isEmpty() ? 100.0 : (upToDateCount * 100.0) / members.size();

            CollectivityInfoDto info = new CollectivityInfoDto();
            info.setName(coll.getName());
            info.setNumber(coll.getNumber() != null ? Integer.valueOf(coll.getNumber()) : null);

            CollectivityOverallStatisticsDto dto = new CollectivityOverallStatisticsDto();
            dto.setCollectivityInformation(info);
            dto.setNewMembersNumber(newMembers);
            dto.setOverallMemberCurrentDuePercentage(percentage);
            result.add(dto);
        }

        return result;
    }

    private double computeExpectedForFee(MembershipFeeEntity fee, LocalDate from, LocalDate to) {
        if (!"ACTIVE".equals(fee.getStatus()) || from.isAfter(to)) {
            return 0;
        }

        LocalDate eligible = fee.getEligibleFrom();
        String frequency = fee.getFrequency();
        double amount = fee.getAmount();

        if ("PUNCTUALLY".equals(frequency)) {
            if (!eligible.isBefore(from) && !eligible.isAfter(to)) {
                return amount;
            }
            return 0;
        }

        double total = 0;
        LocalDate cursor = eligible;
        while (!cursor.isAfter(to)) {
            if (!cursor.isBefore(from)) {
                total += amount;
            }
            switch (frequency) {
                case "WEEKLY":
                    cursor = cursor.plusWeeks(1);
                    break;
                case "MONTHLY":
                    cursor = cursor.plusMonths(1);
                    break;
                case "ANNUALLY":
                    cursor = cursor.plusYears(1);
                    break;
                default:
                    break;
            }
        }
        return total;
    }
}