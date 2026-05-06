package com.collectivity.service;

import com.collectivity.dto.CreateMemberDto;
import com.collectivity.dto.MemberDto;
import com.collectivity.entity.CollectivityEntity;
import com.collectivity.entity.MemberEntity;
import com.collectivity.entity.MemberOccupation;
import com.collectivity.exception.BadRequestException;
import com.collectivity.exception.NotFoundException;
import com.collectivity.repository.CollectivityRepository;
import com.collectivity.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class MemberService {

    private static final long REFEREE_MIN_SENIORITY_DAYS = 90;
    private static final int MIN_REFEREES = 2;
    private static final long REGISTRATION_FEE = 50_000L; // 50 000 MGA

    private final MemberRepository memberRepository;
    private final CollectivityRepository collectivityRepository;

    public MemberService(MemberRepository memberRepository,
                         CollectivityRepository collectivityRepository) {
        this.memberRepository = memberRepository;
        this.collectivityRepository = collectivityRepository;
    }

    // ── POST /members ─────────────────────────────────────────────────────────

    public List<MemberDto> createMembers(List<CreateMemberDto> requests) {
        List<MemberDto> created = new ArrayList<>();
        for (CreateMemberDto request : requests) {
            created.add(createSingleMember(request));
        }
        return created;
    }

    private MemberDto createSingleMember(CreateMemberDto request) {
        // 1. Collectivité existante
        CollectivityEntity collectivity = null;
        if (request.getCollectivityIdentifier() != null) {
            collectivity = collectivityRepository.findById(request.getCollectivityIdentifier())
                    .orElseThrow(() -> new NotFoundException(
                            "Collectivity not found: " + request.getCollectivityIdentifier()));
        }

        // 2. Au moins 2 parrains requis
        if (request.getReferees() == null || request.getReferees().size() < MIN_REFEREES) {
            throw new BadRequestException(
                    "At least " + MIN_REFEREES + " referees are required for admission.");
        }

        // 3. Validation des parrains (occupation, ancienneté, règle collectivité)
        List<MemberEntity> refereeEntities =
                validateRefereesWithCollectivityRule(request.getReferees(), collectivity);

        // 4. Frais d'inscription
        if (Boolean.FALSE.equals(request.getRegistrationFeePaid())) {
            throw new BadRequestException(
                    "Registration fee of " + REGISTRATION_FEE + " MGA has not been paid.");
        }

        // 5. Cotisation annuelle ou cotisation périodique
        if (collectivity != null && collectivity.getAnnualContributionAmount() != null) {
            Long expected = collectivity.getAnnualContributionAmount();
            if (request.getAnnualContributionPaid() == null
                    || request.getAnnualContributionPaid() < expected) {
                throw new BadRequestException(
                        "Annual contribution of " + expected + " MGA must be paid in full. "
                                + "Paid: " + (request.getAnnualContributionPaid() != null
                                ? request.getAnnualContributionPaid() : 0));
            }
        } else if (Boolean.FALSE.equals(request.getMembershipDuesPaid())) {
            throw new BadRequestException("Membership dues have not been paid.");
        }

        // 6. Persistance
        MemberEntity entity = toEntity(request);
        entity.setRefereeRelations(extractRefereeRelations(request.getReferees()));
        memberRepository.save(entity);

        return toDto(entity, refereeEntities);
    }

    // ── Validation parrains ───────────────────────────────────────────────────

    private List<MemberEntity> validateRefereesWithCollectivityRule(
            List<String> refereeIds, CollectivityEntity targetCollectivity) {

        List<MemberEntity> referees = new ArrayList<>();
        int fromTarget = 0;
        int fromOthers = 0;

        for (String refereeId : refereeIds) {
            MemberEntity referee = memberRepository.findById(refereeId)
                    .orElseThrow(() -> new NotFoundException("Referee not found: " + refereeId));

            // Occupation : le JUNIOR ne peut pas parrainer
            if (referee.getOccupation() == MemberOccupation.JUNIOR) {
                throw new BadRequestException(
                        "Referee " + refereeId
                                + " is a junior member and cannot sponsor new members.");
            }

            // Ancienneté minimale de 90 jours
            long seniority = ChronoUnit.DAYS.between(referee.getMembershipDate(), LocalDate.now());
            if (seniority < REFEREE_MIN_SENIORITY_DAYS) {
                throw new BadRequestException(
                        "Referee " + refereeId
                                + " does not yet have the required 90 days of seniority "
                                + "(current: " + seniority + " days).");
            }

            // Comptage par collectivité
            if (targetCollectivity != null
                    && targetCollectivity.getId().equals(referee.getCollectivityId())) {
                fromTarget++;
            } else {
                fromOthers++;
            }

            referees.add(referee);
        }

        // Règle : parrains internes ≥ parrains externes
        if (targetCollectivity != null && fromTarget < fromOthers) {
            throw new BadRequestException(
                    "Admission rule violation: at least as many referees from the target collectivity ("
                            + fromTarget + ") as from other collectivities (" + fromOthers + ") are required.");
        }

        return referees;
    }

    private Map<String, String> extractRefereeRelations(List<String> refereeIds) {
        Map<String, String> map = new HashMap<>();
        for (String id : refereeIds) {
            map.put(id, "UNKNOWN");
        }
        return map;
    }

    private MemberEntity toEntity(CreateMemberDto dto) {
        MemberEntity entity = new MemberEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setBirthDate(dto.getBirthDate());
        entity.setGender(dto.getGender());
        entity.setAddress(dto.getAddress());
        entity.setProfession(dto.getProfession());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setEmail(dto.getEmail());
        entity.setOccupation(dto.getOccupation());
        entity.setCollectivityId(dto.getCollectivityIdentifier());
        entity.setMembershipDate(LocalDate.now());
        entity.setAnnualContributionAmount(dto.getAnnualContributionPaid());

        // dto.getReferees() est maintenant List<String>
        if (dto.getReferees() != null) {
            entity.setRefereeIds(new ArrayList<>(dto.getReferees()));
        } else {
            entity.setRefereeIds(new ArrayList<>());
        }

        return entity;
    }

    public MemberDto toDto(MemberEntity entity) {
        List<MemberEntity> refereeEntities = new ArrayList<>();
        for (String refereeId : entity.getRefereeIds()) {
            memberRepository.findById(refereeId).ifPresent(refereeEntities::add);
        }
        return toDto(entity, refereeEntities);
    }

    private MemberDto toDto(MemberEntity entity, List<MemberEntity> refereeEntities) {
        MemberDto dto = new MemberDto();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setBirthDate(entity.getBirthDate());
        dto.setGender(entity.getGender());
        dto.setAddress(entity.getAddress());
        dto.setProfession(entity.getProfession());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setEmail(entity.getEmail());
        dto.setOccupation(entity.getOccupation());
        dto.setJoinDate(entity.getMembershipDate());

        List<MemberDto> refereeDtos = new ArrayList<>();
        for (MemberEntity referee : refereeEntities) {
            MemberDto refereeDto = new MemberDto();
            refereeDto.setId(referee.getId());
            refereeDto.setFirstName(referee.getFirstName());
            refereeDto.setLastName(referee.getLastName());
            refereeDto.setBirthDate(referee.getBirthDate());
            refereeDto.setGender(referee.getGender());
            refereeDto.setAddress(referee.getAddress());
            refereeDto.setProfession(referee.getProfession());
            refereeDto.setPhoneNumber(referee.getPhoneNumber());
            refereeDto.setEmail(referee.getEmail());
            refereeDto.setOccupation(referee.getOccupation());
            refereeDto.setJoinDate(referee.getMembershipDate());
            refereeDtos.add(refereeDto);
        }
        dto.setReferees(refereeDtos);
        return dto;
    }
}