package com.collectivity.dto;

/**
 * Request DTO pour PATCH /collectivities/{id} — assignation du numéro et du nom.
 * number est un Integer conformément au schéma AssignCollectivityIdentity (v0.0.2+).
 */
public class UpdateCollectivityDto {

    private Integer number;
    private String name;

    public UpdateCollectivityDto() {}

    public String getNumber() { return String.valueOf(number); }
    public void setNumber(Integer number) { this.number = number; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}