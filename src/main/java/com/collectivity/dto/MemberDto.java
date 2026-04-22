package com.collectivity.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Response DTO for Member schema.
 * Referees are expanded to full MemberDto objects (one level deep).
 */
public class MemberDto extends MemberInformationDto {

    private String id;
    private List<MemberDto> referees = new ArrayList<>();

    public MemberDto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public List<MemberDto> getReferees() { return referees; }
    public void setReferees(List<MemberDto> referees) { this.referees = referees; }
}