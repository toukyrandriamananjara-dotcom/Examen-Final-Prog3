package com.collectivity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO de réponse unifié pour le schéma Collectivity (v0.0.3).
 * Couvre les deux endpoints : POST /collectivities et PATCH /collectivities/{id}.
 * - number et name sont null jusqu'à assignation via PATCH.
 * - agriculturalSpecialty et creationDate sont renseignés dès la création.
 */
public class CollectivityDto {

    private String id;
    private Integer number;             // null jusqu'à assignation fédération
    private String name;                // null jusqu'à assignation fédération
    private String location;
    private String agriculturalSpecialty;  // v0.0.3

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate creationDate;     // v0.0.3 — positionné automatiquement à la création

    private CollectivityStructureDto structure;
    private List<MemberDto> members = new ArrayList<>();

    public CollectivityDto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Integer getNumber() { return number; }
    public void setNumber(Integer number) { this.number = number; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getAgriculturalSpecialty() { return agriculturalSpecialty; }
    public void setAgriculturalSpecialty(String agriculturalSpecialty) { this.agriculturalSpecialty = agriculturalSpecialty; }

    public LocalDate getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDate creationDate) { this.creationDate = creationDate; }

    public CollectivityStructureDto getStructure() { return structure; }
    public void setStructure(CollectivityStructureDto structure) { this.structure = structure; }

    public List<MemberDto> getMembers() { return members; }
    public void setMembers(List<MemberDto> members) { this.members = members; }
}