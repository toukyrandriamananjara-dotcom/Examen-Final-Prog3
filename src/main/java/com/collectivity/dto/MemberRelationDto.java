package com.collectivity.dto;


public class MemberRelationDto {
    private String refereeId;
    private String relationType;

    public MemberRelationDto() {}

    public String getRefereeId() { return refereeId; }
    public void setRefereeId(String refereeId) { this.refereeId = refereeId; }

    public String getRelationType() { return relationType; }
    public void setRelationType(String relationType) { this.relationType = relationType; }
}