package com.collectivity.service;

import com.collectivity.dto.CreateMembershipFeeDto;
import com.collectivity.dto.MembershipFeeDto;
import com.collectivity.entity.CollectivityEntity;
import com.collectivity.entity.MembershipFeeEntity;
import com.collectivity.exception.BadRequestException;
import com.collectivity.exception.NotFoundException;
import com.collectivity.repository.CollectivityRepository;
import com.collectivity.repository.MembershipFeeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MembershipFeeService {

    private final MembershipFeeRepository membershipFeeRepository;
    private final CollectivityRepository collectivityRepository;

    public MembershipFeeService(MembershipFeeRepository membershipFeeRepository,
                                CollectivityRepository collectivityRepository) {
        this.membershipFeeRepository = membershipFeeRepository;
        this.collectivityRepository = collectivityRepository;
    }

    public List<MembershipFeeDto> getMembershipFeesByCollectivity(String collectivityId) {
        CollectivityEntity collectivity = collectivityRepository.findById(collectivityId)
                .orElseThrow(() -> new NotFoundException("Collectivity not found: " + collectivityId));

        return membershipFeeRepository.findByCollectivityId(collectivityId).stream()
                .map(this::toDto)
                .toList();
    }

    public List<MembershipFeeDto> createMembershipFees(String collectivityId, List<CreateMembershipFeeDto> requests) {
        CollectivityEntity collectivity = collectivityRepository.findById(collectivityId)
                .orElseThrow(() -> new NotFoundException("Collectivity not found: " + collectivityId));

        List<MembershipFeeDto> created = new ArrayList<>();
        for (CreateMembershipFeeDto request : requests) {
            created.add(createSingleMembershipFee(collectivityId, request));
        }
        return created;
    }

    private MembershipFeeDto createSingleMembershipFee(String collectivityId, CreateMembershipFeeDto request) {
        // Validate frequency
        String frequency = request.getFrequency();
        if (!isValidFrequency(frequency)) {
            throw new BadRequestException("Unrecognized frequency: " + frequency + ". Valid values: WEEKLY, MONTHLY, ANNUALLY, PUNCTUALLY");
        }

        // Validate amount
        if (request.getAmount() < 0) {
            throw new BadRequestException("Amount cannot be negative: " + request.getAmount());
        }

        MembershipFeeEntity entity = new MembershipFeeEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setCollectivityId(collectivityId);
        entity.setEligibleFrom(request.getEligibleFrom());
        entity.setFrequency(frequency);
        entity.setAmount(request.getAmount());
        entity.setLabel(request.getLabel());
        entity.setStatus("ACTIVE");

        membershipFeeRepository.save(entity);
        return toDto(entity);
    }

    private boolean isValidFrequency(String frequency) {
        return frequency != null && (frequency.equals("WEEKLY") || frequency.equals("MONTHLY") ||
                frequency.equals("ANNUALLY") || frequency.equals("PUNCTUALLY"));
    }

    private MembershipFeeDto toDto(MembershipFeeEntity entity) {
        MembershipFeeDto dto = new MembershipFeeDto();
        dto.setId(entity.getId());
        dto.setCollectivityId(entity.getCollectivityId());
        dto.setEligibleFrom(entity.getEligibleFrom());
        dto.setFrequency(entity.getFrequency());
        dto.setAmount(entity.getAmount());
        dto.setLabel(entity.getLabel());
        dto.setStatus(entity.getStatus());
        return dto;
    }
}