package com.collectivity.dto;

/**
 * Response DTO for CollectivityStructure schema.
 * Each position holds a full MemberDto.
 */
public class CollectivityStructureDto {

    private MemberDto president;
    private MemberDto vicePresident;
    private MemberDto treasurer;
    private MemberDto secretary;

    public CollectivityStructureDto() {}

    public MemberDto getPresident() { return president; }
    public void setPresident(MemberDto president) { this.president = president; }

    public MemberDto getVicePresident() { return vicePresident; }
    public void setVicePresident(MemberDto vicePresident) { this.vicePresident = vicePresident; }

    public MemberDto getTreasurer() { return treasurer; }
    public void setTreasurer(MemberDto treasurer) { this.treasurer = treasurer; }

    public MemberDto getSecretary() { return secretary; }
    public void setSecretary(MemberDto secretary) { this.secretary = secretary; }
}