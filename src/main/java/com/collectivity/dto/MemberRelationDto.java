package com.collectivity.dto;

/**
 * DTO for the relation between a member and their referee.
 */
public class MemberRelationDto {
    private String refereeId;
    private String relationType; // "FAMILY", "FRIEND", "COLLEAGUE", etc.

    public MemberRelationDto() {}

    public String getRefereeId() { return refereeId; }
    public void setRefereeId(String refereeId) { this.refereeId = refereeId; }

    public String getRelationType() { return relationType; }
    public void setRelationType(String relationType) { this.relationType = relationType; }
}