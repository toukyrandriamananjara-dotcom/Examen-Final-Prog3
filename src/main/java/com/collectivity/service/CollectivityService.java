package com.collectivity.service;

import com.collectivity.dto.*;
import com.collectivity.entity.CollectivityEntity;
import com.collectivity.entity.MemberEntity;
import com.collectivity.exception.BadRequestException;
import com.collectivity.exception.NotFoundException;
import com.collectivity.repository.CollectivityRepository;
import com.collectivity.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CollectivityService {

    /**
     * A collectivity must have at least 10 members,
     * of which at least 5 must have been in the federation for 6+ months.
     */
    private static final int MIN_MEMBERS = 10;
    private static final int MIN_SENIOR_MEMBERS = 5;
    private static final long SENIOR_SENIORITY_DAYS = 180; // ~6 months

    private final CollectivityRepository collectivityRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    public CollectivityService(CollectivityRepository collectivityRepository,
                               MemberRepository memberRepository,
                               MemberService memberService) {
        this.collectivityRepository = collectivityRepository;
        this.memberRepository = memberRepository;
        this.memberService = memberService;
    }

    /**
     * Creates a batch of collectivities after validating business rules.
     *
     * @throws NotFoundException    if any referenced member is not found (HTTP 404)
     * @throws BadRequestException  if federation approval is missing, structure is absent,
     *                              or member count / seniority requirements are not met (HTTP 400)
     */
    public List<CollectivityDto> createCollectivities(List<CreateCollectivityDto> requests) {
        List<CollectivityDto> created = new ArrayList<>();
        for (CreateCollectivityDto request : requests) {
            created.add(createSingleCollectivity(request));
        }
        return created;
    }

    // ─────────────────────────────────────────────────────────────────────────

    private CollectivityDto createSingleCollectivity(CreateCollectivityDto request) {

        // 1. Federation approval required (400)
        if (!Boolean.TRUE.equals(request.getFederationApproval())) {
            throw new BadRequestException("Federation approval is required to open a new collectivity.");
        }

        // 2. Structure required (400)
        if (request.getStructure() == null) {
            throw new BadRequestException("A governance structure (president, vice-president, treasurer, secretary) is required.");
        }
        validateStructureNotEmpty(request.getStructure());

        // 3. Resolve all members (404 if any ID is unknown)
        List<MemberEntity> memberEntities = resolveMembers(request.getMembers());

        // 4. Minimum 10 members (400)
        if (memberEntities.size() < MIN_MEMBERS) {
            throw new BadRequestException(
                    "A collectivity must have at least " + MIN_MEMBERS + " members "
                            + "(provided: " + memberEntities.size() + ").");
        }

        // 5. At least 5 members with 6+ months of seniority in the federation (400)
        long seniorCount = memberEntities.stream()
                .filter(m -> m.getMembershipDate() != null
                        && ChronoUnit.DAYS.between(m.getMembershipDate(), LocalDate.now()) >= SENIOR_SENIORITY_DAYS)
                .count();
        if (seniorCount < MIN_SENIOR_MEMBERS) {
            throw new BadRequestException(
                    "At least " + MIN_SENIOR_MEMBERS + " members must have 6+ months of federation seniority "
                            + "(current: " + seniorCount + ").");
        }

        // 6. Resolve structure members (404 if any ID is unknown)
        CreateCollectivityStructureDto structureRequest = request.getStructure();
        MemberEntity president    = resolveMember(structureRequest.getPresident(),    "President");
        MemberEntity vicePresident = resolveMember(structureRequest.getVicePresident(), "Vice-president");
        MemberEntity treasurer    = resolveMember(structureRequest.getTreasurer(),    "Treasurer");
        MemberEntity secretary    = resolveMember(structureRequest.getSecretary(),    "Secretary");

        // 7. Build and persist the entity
        CollectivityEntity entity = new CollectivityEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setLocation(request.getLocation());
        entity.setFederationApproval(true);
        entity.setCreationDate(LocalDate.now());
        entity.setPresidentId(president.getId());
        entity.setVicePresidentId(vicePresident.getId());
        entity.setTreasurerId(treasurer.getId());
        entity.setSecretaryId(secretary.getId());

        List<String> memberIds = new ArrayList<>();
        for (MemberEntity m : memberEntities) {
            memberIds.add(m.getId());
        }
        entity.setMemberIds(memberIds);

        collectivityRepository.save(entity);

        return toDto(entity, memberEntities, president, vicePresident, treasurer, secretary);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Validation helpers
    // ─────────────────────────────────────────────────────────────────────────

    private void validateStructureNotEmpty(CreateCollectivityStructureDto structure) {
        if (structure.getPresident() == null || structure.getPresident().isBlank()) {
            throw new BadRequestException("Structure is missing a president.");
        }
        if (structure.getVicePresident() == null || structure.getVicePresident().isBlank()) {
            throw new BadRequestException("Structure is missing a vice-president.");
        }
        if (structure.getTreasurer() == null || structure.getTreasurer().isBlank()) {
            throw new BadRequestException("Structure is missing a treasurer.");
        }
        if (structure.getSecretary() == null || structure.getSecretary().isBlank()) {
            throw new BadRequestException("Structure is missing a secretary.");
        }
    }

    private List<MemberEntity> resolveMembers(List<String> memberIds) {
        List<MemberEntity> entities = new ArrayList<>();
        if (memberIds == null) return entities;
        for (String id : memberIds) {
            entities.add(resolveMember(id, "Member"));
        }
        return entities;
    }

    private MemberEntity resolveMember(String id, String role) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(role + " not found with id: " + id));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Mapping helpers
    // ─────────────────────────────────────────────────────────────────────────

    private CollectivityDto toDto(CollectivityEntity entity,
                                  List<MemberEntity> members,
                                  MemberEntity president,
                                  MemberEntity vicePresident,
                                  MemberEntity treasurer,
                                  MemberEntity secretary) {
        CollectivityDto dto = new CollectivityDto();
        dto.setId(entity.getId());
        dto.setLocation(entity.getLocation());

        CollectivityStructureDto structureDto = new CollectivityStructureDto();
        structureDto.setPresident(memberService.toDto(president));
        structureDto.setVicePresident(memberService.toDto(vicePresident));
        structureDto.setTreasurer(memberService.toDto(treasurer));
        structureDto.setSecretary(memberService.toDto(secretary));
        dto.setStructure(structureDto);

        List<MemberDto> memberDtos = new ArrayList<>();
        for (MemberEntity m : members) {
            memberDtos.add(memberService.toDto(m));
        }
        dto.setMembers(memberDtos);

        return dto;
    }
}