package com.collectivity.dto;

/**
 * Response DTO for Collectivity with number and name.
 */
public class CollectivityResponseDto extends CollectivityDto {
    private String number;
    private String name;
    private String agriculturalSpecialty;

    public CollectivityResponseDto() {}

    //public Integer getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAgriculturalSpecialty() { return agriculturalSpecialty; }
    public void setAgriculturalSpecialty(String agriculturalSpecialty) { this.agriculturalSpecialty = agriculturalSpecialty; }
}