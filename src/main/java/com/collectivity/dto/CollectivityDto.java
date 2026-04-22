package com.collectivity.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Response DTO for Collectivity schema.
 */
public class CollectivityDto {

    private String id;
    private String location;
    private CollectivityStructureDto structure;
    private List<MemberDto> members = new ArrayList<>();

    public CollectivityDto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public CollectivityStructureDto getStructure() { return structure; }
    public void setStructure(CollectivityStructureDto structure) { this.structure = structure; }

    public List<MemberDto> getMembers() { return members; }
    public void setMembers(List<MemberDto> members) { this.members = members; }
}