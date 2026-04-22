package com.collectivity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class MemberDto extends MemberInformationDto {

    private String id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate joinDate;

    private List<MemberDto> referees = new ArrayList<>();

    public MemberDto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public LocalDate getJoinDate() { return joinDate; }
    public void setJoinDate(LocalDate joinDate) { this.joinDate = joinDate; }

    public List<MemberDto> getReferees() { return referees; }
    public void setReferees(List<MemberDto> referees) { this.referees = referees; }
}