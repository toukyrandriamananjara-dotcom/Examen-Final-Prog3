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

    private static final int MIN_MEMBERS = 10;
    private static final int MIN_SENIOR_MEMBERS = 5;
    private static final long SENIOR_SENIORITY_DAYS = 180;

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

    // ── POST /collectivities ──────────────────────────────────────────────────

    public List<CollectivityDto> createCollectivities(List<CreateCollectivityDto> requests) {
        List<CollectivityDto> created = new ArrayList<>();
        for (CreateCollectivityDto request : requests) {
            created.add(createSingleCollectivity(request));
        }
        return created;
    }

    // ── PATCH /collectivities/{id} ────────────────────────────────────────────

    /**
     * Fonctionnalité J — Assignation du numéro et du nom unique par la fédération.
     * Retourne désormais un CollectivityDto unifié (v0.0.3).
     */
    public CollectivityDto updateCollectivity(String id, UpdateCollectivityDto request) {
        CollectivityEntity entity = collectivityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Collectivity not found with id: " + id));

        // Numéro déjà assigné : interdit de le modifier
        if (entity.getNumber() != null && request.getNumber() != null
                && !entity.getNumber().equals(request.getNumber())) {
            throw new BadRequestException(
                    "Collectivity number cannot be changed once assigned. Current number: " + entity.getNumber());
        }

        // Nom déjà assigné : interdit de le modifier
        if (entity.getName() != null && request.getName() != null
                && !entity.getName().equals(request.getName())) {
            throw new BadRequestException(
                    "Collectivity name cannot be changed once assigned. Current name: " + entity.getName());
        }

        // Unicité du numéro
        if (request.getNumber() != null && !request.getNumber().equals(entity.getNumber())) {
            boolean numberExists = collectivityRepository.findAll().stream()
                    .anyMatch(c -> request.getNumber().equals(c.getNumber()));
            if (numberExists) {
                throw new BadRequestException("Collectivity number already exists: " + request.getNumber());
            }
            entity.setNumber(request.getNumber());
        }

        // Unicité du nom (insensible à la casse)
        if (request.getName() != null && !request.getName().equals(entity.getName())) {
            boolean nameExists = collectivityRepository.findAll().stream()
                    .anyMatch(c -> request.getName().equalsIgnoreCase(c.getName()));
            if (nameExists) {
                throw new BadRequestException("Collectivity name already exists: " + request.getName());
            }
            entity.setName(request.getName());
        }

        collectivityRepository.save(entity);
        return toDto(entity);
    }

    // ── Logique de création ───────────────────────────────────────────────────

    private CollectivityDto createSingleCollectivity(CreateCollectivityDto request) {
        if (!Boolean.TRUE.equals(request.getFederationApproval())) {
            throw new BadRequestException("Federation approval is required to open a new collectivity.");
        }

        if (request.getStructure() == null) {
            throw new BadRequestException(
                    "A governance structure (president, deputy president, treasurer, secretary) is required.");
        }
        validateStructureNotEmpty(request.getStructure());

        List<MemberEntity> memberEntities = resolveMembers(request.getMembers());

        if (memberEntities.size() < MIN_MEMBERS) {
            throw new BadRequestException(
                    "A collectivity must have at least " + MIN_MEMBERS + " members "
                            + "(provided: " + memberEntities.size() + ").");
        }

        long seniorCount = memberEntities.stream()
                .filter(m -> m.getMembershipDate() != null
                        && ChronoUnit.DAYS.between(m.getMembershipDate(), LocalDate.now()) >= SENIOR_SENIORITY_DAYS)
                .count();
        if (seniorCount < MIN_SENIOR_MEMBERS) {
            throw new BadRequestException(
                    "At least " + MIN_SENIOR_MEMBERS + " members must have 6+ months of federation seniority "
                            + "(current: " + seniorCount + ").");
        }

        CreateCollectivityStructureDto structureRequest = request.getStructure();
        MemberEntity president      = resolveMember(structureRequest.getPresident(),       "President");
        MemberEntity deputyPresident = resolveMember(structureRequest.getDeputyPresident(), "Deputy president");
        MemberEntity treasurer      = resolveMember(structureRequest.getTreasurer(),       "Treasurer");
        MemberEntity secretary      = resolveMember(structureRequest.getSecretary(),       "Secretary");

        CollectivityEntity entity = new CollectivityEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setLocation(request.getLocation());
        entity.setAgriculturalSpecialty(request.getAgriculturalSpecialty());
        entity.setFederationApproval(true);
        entity.setCreationDate(LocalDate.now());
        entity.setPresidentId(president.getId());
        entity.setDeputyPresidentId(deputyPresident.getId());
        entity.setTreasurerId(treasurer.getId());
        entity.setSecretaryId(secretary.getId());

        List<String> memberIds = new ArrayList<>();
        for (MemberEntity m : memberEntities) {
            memberIds.add(m.getId());
        }
        entity.setMemberIds(memberIds);

        collectivityRepository.save(entity);

        return toDto(entity, memberEntities, president, deputyPresident, treasurer, secretary);
    }

    private void validateStructureNotEmpty(CreateCollectivityStructureDto structure) {
        if (structure.getPresident() == null || structure.getPresident().isBlank()) {
            throw new BadRequestException("Structure is missing a president.");
        }
        if (structure.getDeputyPresident() == null || structure.getDeputyPresident().isBlank()) {
            throw new BadRequestException("Structure is missing a deputy president.");
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

    // ── Mappage entité → DTO ──────────────────────────────────────────────────

    /**
     * Mapping complet utilisé lors de la création (members et structure résolus).
     */
    private CollectivityDto toDto(CollectivityEntity entity,
                                  List<MemberEntity> members,
                                  MemberEntity president,
                                  MemberEntity deputyPresident,
                                  MemberEntity treasurer,
                                  MemberEntity secretary) {
        CollectivityDto dto = buildBaseDto(entity);

        CollectivityStructureDto structureDto = new CollectivityStructureDto();
        structureDto.setPresident(memberService.toDto(president));
        structureDto.setDeputyPresident(memberService.toDto(deputyPresident));
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

    /**
     * Mapping léger utilisé après PATCH (structure/members rechargés depuis le store).
     */
    private CollectivityDto toDto(CollectivityEntity entity) {
        CollectivityDto dto = buildBaseDto(entity);

        // Résolution de la structure depuis le store
        if (entity.getPresidentId() != null) {
            CollectivityStructureDto structureDto = new CollectivityStructureDto();
            memberRepository.findById(entity.getPresidentId())
                    .ifPresent(m -> structureDto.setPresident(memberService.toDto(m)));
            memberRepository.findById(entity.getDeputyPresidentId())
                    .ifPresent(m -> structureDto.setDeputyPresident(memberService.toDto(m)));
            memberRepository.findById(entity.getTreasurerId())
                    .ifPresent(m -> structureDto.setTreasurer(memberService.toDto(m)));
            memberRepository.findById(entity.getSecretaryId())
                    .ifPresent(m -> structureDto.setSecretary(memberService.toDto(m)));
            dto.setStructure(structureDto);
        }

        // Résolution des membres depuis le store
        List<MemberDto> memberDtos = new ArrayList<>();
        for (String memberId : entity.getMemberIds()) {
            memberRepository.findById(memberId)
                    .ifPresent(m -> memberDtos.add(memberService.toDto(m)));
        }
        dto.setMembers(memberDtos);

        return dto;
    }

    /**
     * Champs scalaires communs aux deux variantes de mapping.
     */
    private CollectivityDto buildBaseDto(CollectivityEntity entity) {
        CollectivityDto dto = new CollectivityDto();
        dto.setId(entity.getId());
        dto.setNumber(entity.getNumber() != null ? Integer.valueOf(entity.getNumber()) : null);
        dto.setName(entity.getName());
        dto.setLocation(entity.getLocation());
        dto.setAgriculturalSpecialty(entity.getAgriculturalSpecialty());
        dto.setCreationDate(entity.getCreationDate());
        return dto;
    }
}