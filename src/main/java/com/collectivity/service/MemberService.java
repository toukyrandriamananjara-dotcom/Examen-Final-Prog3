package com.collectivity.service;

import com.collectivity.dto.CreateMemberDto;
import com.collectivity.dto.MemberDto;
import com.collectivity.entity.MemberEntity;
import com.collectivity.entity.MemberOccupation;
import com.collectivity.exception.BadRequestException;
import com.collectivity.exception.NotFoundException;
import com.collectivity.repository.CollectivityRepository;
import com.collectivity.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MemberService {

    /**
     * A referee must have been a member for at least 90 days
     * and must not be a junior member.
     */
    private static final long REFEREE_MIN_SENIORITY_DAYS = 90;

    private final MemberRepository memberRepository;
    private final CollectivityRepository collectivityRepository;

    public MemberService(MemberRepository memberRepository,
                         CollectivityRepository collectivityRepository) {
        this.memberRepository = memberRepository;
        this.collectivityRepository = collectivityRepository;
    }

    /**
     * Creates a batch of members after validating business rules.
     *
     * @throws NotFoundException    if collectivity or any referee is not found (HTTP 404)
     * @throws BadRequestException  if payment flags are false or referees are invalid (HTTP 400)
     */
    public List<MemberDto> createMembers(List<CreateMemberDto> requests) {
        List<MemberDto> created = new ArrayList<>();
        for (CreateMemberDto request : requests) {
            created.add(createSingleMember(request));
        }
        return created;
    }

    // ─────────────────────────────────────────────────────────────────────────

    private MemberDto createSingleMember(CreateMemberDto request) {

        // 1. Validate collectivity exists (404)
        if (request.getCollectivityIdentifier() != null) {
            collectivityRepository.findById(request.getCollectivityIdentifier())
                    .orElseThrow(() -> new NotFoundException(
                            "Collectivity not found: " + request.getCollectivityIdentifier()));
        }

        // 2. Validate referees exist and meet criteria (404 then 400)
        List<MemberEntity> refereeEntities = resolveReferees(request.getReferees());

        // 3. Validate payment flags (400)
        if (Boolean.FALSE.equals(request.getRegistrationFeePaid())) {
            throw new BadRequestException("Registration fee has not been paid.");
        }
        if (Boolean.FALSE.equals(request.getMembershipDuesPaid())) {
            throw new BadRequestException("Membership dues have not been paid.");
        }

        // 4. Build and persist the entity
        MemberEntity entity = toEntity(request);
        memberRepository.save(entity);

        return toDto(entity, refereeEntities);
    }

    /**
     * Resolves each referee ID: throws 404 if unknown, 400 if not a confirmed member
     * or does not yet have the required seniority.
     */
    private List<MemberEntity> resolveReferees(List<String> refereeIds) {
        List<MemberEntity> referees = new ArrayList<>();
        if (refereeIds == null || refereeIds.isEmpty()) {
            return referees;
        }
        for (String refereeId : refereeIds) {
            MemberEntity referee = memberRepository.findById(refereeId)
                    .orElseThrow(() -> new NotFoundException("Referee not found: " + refereeId));

            if (referee.getOccupation() == MemberOccupation.JUNIOR) {
                throw new BadRequestException(
                        "Referee " + refereeId + " is a junior member and cannot sponsor new members.");
            }

            long seniority = java.time.temporal.ChronoUnit.DAYS.between(
                    referee.getMembershipDate(), LocalDate.now());
            if (seniority < REFEREE_MIN_SENIORITY_DAYS) {
                throw new BadRequestException(
                        "Referee " + refereeId + " does not yet have the required 90 days of seniority "
                                + "(current: " + seniority + " days).");
            }

            referees.add(referee);
        }
        return referees;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Mapping helpers
    // ─────────────────────────────────────────────────────────────────────────

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
        entity.setRefereeIds(dto.getReferees() != null ? dto.getReferees() : new ArrayList<>());
        entity.setMembershipDate(LocalDate.now());
        return entity;
    }

    /**
     * Converts a MemberEntity to its DTO representation.
     * Referees are expanded one level deep (their own referees are left empty to avoid cycles).
     */
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

        List<MemberDto> refereeDtos = new ArrayList<>();
        for (MemberEntity referee : refereeEntities) {
            // Shallow mapping — referees of referees are not expanded (prevents cycles)
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
            refereeDtos.add(refereeDto);
        }
        dto.setReferees(refereeDtos);
        return dto;
    }
}