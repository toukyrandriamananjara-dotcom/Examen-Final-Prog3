package com.collectivity.dto;

/**
 * Response DTO pour le schéma CollectivityStructure.
 * Chaque position expose un MemberDto complet.
 */
public class CollectivityStructureDto {

    private MemberDto president;
    private MemberDto deputyPresident;  // renommé depuis vicePresident (v0.0.3)
    private MemberDto treasurer;
    private MemberDto secretary;

    public CollectivityStructureDto() {}

    public MemberDto getPresident() { return president; }
    public void setPresident(MemberDto president) { this.president = president; }

    public MemberDto getDeputyPresident() { return deputyPresident; }
    public void setDeputyPresident(MemberDto deputyPresident) { this.deputyPresident = deputyPresident; }

    public MemberDto getTreasurer() { return treasurer; }
    public void setTreasurer(MemberDto treasurer) { this.treasurer = treasurer; }

    public MemberDto getSecretary() { return secretary; }
    public void setSecretary(MemberDto secretary) { this.secretary = secretary; }
}