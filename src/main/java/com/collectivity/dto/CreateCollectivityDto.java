package com.collectivity.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Request DTO for POST /collectivities — mirrors the OpenAPI CreateCollectivity schema.
 */
public class CreateCollectivityDto {

    private String location;
    private List<String> members = new ArrayList<>();
    private Boolean federationApproval;
    private CreateCollectivityStructureDto structure;

    public CreateCollectivityDto() {}

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public List<String> getMembers() { return members; }
    public void setMembers(List<String> members) { this.members = members; }

    public Boolean getFederationApproval() { return federationApproval; }
    public void setFederationApproval(Boolean federationApproval) { this.federationApproval = federationApproval; }

    public CreateCollectivityStructureDto getStructure() { return structure; }
    public void setStructure(CreateCollectivityStructureDto structure) { this.structure = structure; }
}