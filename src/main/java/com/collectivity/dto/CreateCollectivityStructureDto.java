package com.collectivity.dto;

/**
 * Request DTO for CreateCollectivityStructure schema.
 * Each field is a MemberIdentifier (String).
 */
public class CreateCollectivityStructureDto {

    private String president;
    private String vicePresident;
    private String treasurer;
    private String secretary;

    public CreateCollectivityStructureDto() {}

    public String getPresident() { return president; }
    public void setPresident(String president) { this.president = president; }

    public String getVicePresident() { return vicePresident; }
    public void setVicePresident(String vicePresident) { this.vicePresident = vicePresident; }

    public String getTreasurer() { return treasurer; }
    public void setTreasurer(String treasurer) { this.treasurer = treasurer; }

    public String getSecretary() { return secretary; }
    public void setSecretary(String secretary) { this.secretary = secretary; }
}